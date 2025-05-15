from fastapi import FastAPI, Depends, APIRouter, HTTPException, Query
from sqlalchemy import create_engine, Column, Integer, String, DateTime, ForeignKey, Float, Boolean
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session, relationship
from sqlalchemy import Date, DateTime, TIMESTAMP
from datetime import datetime
from pydantic import BaseModel
from typing import Optional, List
from datetime import date


# Configuración de la base de datos de TeamBox
SQLALCHEMY_DATABASE_URL = "mariadb+mariadbconnector://sandra:sandra123@localhost/teambox"

# Crear el motor de la base de datos
engine = create_engine(SQLALCHEMY_DATABASE_URL)

# Crear la clase base para los modelos
Base = declarative_base()

# Crear la sesión
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


# Crear el router de esta API
api_teambox = APIRouter()

# Dependencia para obtener la sesión de la base de datos
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
        
        
        
#........INICIO DE SESION

# Modelo Pydantic para el login

class LoginRequest(BaseModel):
    email: str
    contrasena: str

# Inicio de sesion comprobando email y contraseña

@api_teambox.post("/Usuarios/Login")
def login_usuario(usuario: LoginRequest, db: Session = Depends(get_db)):
    db_usuario = db.query(UsuarioApp).filter(UsuarioApp.email == usuario.email).first()

    if not db_usuario:
        raise HTTPException(status_code=401, detail="Correo no registrado")

    # Verifica si la contraseña coincide
    if db_usuario.contrasena != usuario.contrasena:
        raise HTTPException(status_code=401, detail="Contraseña incorrecta")

    # Responde solo con los datos relevantes para el login
    return {
        "id": db_usuario.id_usuario,
        "nombre": db_usuario.nombre,
        "email": db_usuario.email,
        "esClub": bool(db_usuario.es_club),
        "esPromotor": bool(db_usuario.es_promotor)
    }



        
#........ADMINISTRADORES

# Modelo de SQLAlchemy para Administradores
class Administrador(Base):
    __tablename__ = 'administradores'

    id = Column(Integer, primary_key=True, autoincrement=True)
    nombre_usuario = Column(String(50), nullable=False, unique=True)
    contrasena = Column(String(255), nullable=False)
    nombre_completo = Column(String(100))
    correo_electronico = Column(String(100), nullable=False, unique=True)
    fecha_creacion = Column(TIMESTAMP, server_default='CURRENT_TIMESTAMP')
    ultima_conexion = Column(DateTime, nullable=True)
    

# Ruta para obtener todos los administradores  
@api_teambox.get("/Administradores")
def get_administradores(db: Session = Depends(get_db)):
    administradores = db.query(Administrador).all()
    return [
        {
            "id": a.id,
            "nombre_usuario": a.nombre_usuario,
            "nombre_completo": a.nombre_completo,
            "correo_electronico": a.correo_electronico,
            "fecha_creacion": a.fecha_creacion,
            "ultima_conexion": a.ultima_conexion,
        }
        for a in administradores
    ]
 
 
# Modelo Pydantic para la creación de un administrador
class AdministradorCreate(BaseModel):
    nombre_usuario: str 
    contrasena: str 
    nombre_completo: str
    correo_electronico: str
    
# Método crear administradores
@api_teambox.post("/Administradores/Crear")
def create_administrador(administrador: AdministradorCreate, db: Session = Depends(get_db)):
    try:
        nuevo_admin = Administrador(
            nombre_usuario=administrador.nombre_usuario,
            contrasena=administrador.contrasena,  # ⚠️ contraseña en texto plano
            nombre_completo=administrador.nombre_completo,
            correo_electronico=administrador.correo_electronico,
            fecha_creacion=datetime.utcnow()
        )

        db.add(nuevo_admin)
        db.commit()
        db.refresh(nuevo_admin)

        return {"mensaje": "Administrador creado correctamente", "id": nuevo_admin.id}
    except Exception as e:
        db.rollback()
        return {"error": str(e)}

#........USUARIOS

#  Modelo de SQLAlchemy para Usuarios
class UsuarioApp(Base):
    __tablename__ = "usuarios_app"

    id_usuario = Column(Integer, primary_key=True, index=True)
    nombre = Column(String(100), nullable=False)
    apellido = Column(String(100), nullable=False)
    email = Column(String(100), nullable=False, unique=True)
    contrasena = Column(String(255), nullable=False)  # Debe ir hasheada
    es_club = Column(Integer, default=0)
    es_promotor = Column(Integer, default=0)
    es_boxeador = Column(Integer, default=0)
    nombre_club = Column(String(100), nullable=True)
    logo_club = Column(String(1000000), nullable=True)
    nombre_promotora = Column(String(100), nullable=True)
    logo_promotora = Column(String(1000000), nullable=True)
    comunidad = Column(String(100), nullable=True)
    provincia = Column(String(100), nullable=True)
    telefono1 = Column(String(20), nullable=True)
    telefono2 = Column(String(20), nullable=True)
    telefono3 = Column(String(20), nullable=True)
    foto_perfil = Column(String(1000000), nullable=True)  
    fecha_creacion = Column(TIMESTAMP, server_default='CURRENT_TIMESTAMP')
    # Relación bidireccional
    boxeadores = relationship("Boxeador", back_populates="club")
    
# Modelo Pydantic para la creación de un usuario
class UsuarioCreate(BaseModel):
    nombre: str
    apellido: str
    email: str
    contrasena: str
    es_club: bool = False
    es_promotor: bool = False
    es_boxeador: bool = False  
    nombre_club: Optional[str] = None
    logo_club: Optional[str] = None
    nombre_promotora: Optional[str] = None
    logo_promotora: Optional[str] = None
    comunidad: Optional[str] = None
    provincia: Optional[str] = None
    telefonos: List[str] = []  # Lista de teléfonos
    foto_perfil: Optional[str] = None  
     
    class Config:
        from_attributes = True
        

# Modelo Pydantic para la respuesta del usuario
class UsuarioOut(BaseModel):
    id_usuario: int
    nombre: str
    apellido: str
    email: str
    es_club: bool
    es_promotor: bool
    es_boxeador: bool
    nombre_club: Optional[str] = None
    logo_club: Optional[str] = None
    nombre_promotora: Optional[str] = None
    logo_promotora: Optional[str] = None
    comunidad: str
    provincia: str
    telefono1: Optional[str] = None
    telefono2: Optional[str] = None
    telefono3: Optional[str] = None
    foto_perfil: Optional[str] = None
    fecha_creacion: str

    class Config:
        from_attributes = True
        
        
    
# Método para crear usuarios
@api_teambox.post("/Usuarios/Crear")
def create_usuario(usuario: UsuarioCreate, db: Session = Depends(get_db)):
    # Verifica si ya existe el email
    if db.query(UsuarioApp).filter(UsuarioApp.email == usuario.email).first():
        raise HTTPException(status_code=400, detail="El email ya está registrado")

    nuevo_usuario = UsuarioApp(
        nombre=usuario.nombre,
        apellido=usuario.apellido,
        email=usuario.email,
        contrasena=usuario.contrasena,  
        es_club=int(usuario.es_club),
        es_promotor=int(usuario.es_promotor),
        es_boxeador=int(usuario.es_boxeador),  # Añadido el campo para saber si es boxeador
        nombre_club=usuario.nombre_club,
        logo_club=usuario.logo_club,
        nombre_promotora=usuario.nombre_promotora,
        logo_promotora=usuario.logo_promotora,
        comunidad=usuario.comunidad,
        provincia=usuario.provincia,
        telefono1=usuario.telefonos[0] if len(usuario.telefonos) > 0 else None,
        telefono2=usuario.telefonos[1] if len(usuario.telefonos) > 1 else None,
        telefono3=usuario.telefonos[2] if len(usuario.telefonos) > 2 else None,
        foto_perfil=usuario.foto_perfil  # Añadido el campo para la foto de perfil
    )

    try:
        db.add(nuevo_usuario)
        db.commit()
        db.refresh(nuevo_usuario)  # OK en MariaDB si no usas RETURNING directamente
        return {"mensaje": "Usuario registrado correctamente", "id": nuevo_usuario.id_usuario}
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=400, detail="Error al crear el usuario: " + str(e))



# Método para obtener un usuario por email
@api_teambox.get("/Usuarios/Obtener/{email}", response_model=UsuarioOut)
def obtener_Usuario_Por_Email(email: str, db: Session = Depends(get_db)):
    db_usuario = db.query(UsuarioApp).filter(UsuarioApp.email == email).first()
    
    if db_usuario is None:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")
    
    # Convertir la fecha de creación a un formato adecuado
    fecha_creacion_formateada = db_usuario.fecha_creacion.strftime("%d/%m/%Y %H:%M:%S")

    return UsuarioOut(
        id_usuario=db_usuario.id_usuario,
        nombre=db_usuario.nombre,
        apellido=db_usuario.apellido,
        email=db_usuario.email,
        es_club=bool(db_usuario.es_club) if db_usuario.es_club is not None else None,
        es_promotor=bool(db_usuario.es_promotor) if db_usuario.es_promotor is not None else None,
        es_boxeador=bool(db_usuario.es_boxeador) if db_usuario.es_boxeador is not None else None,
        nombre_club=db_usuario.nombre_club if db_usuario.nombre_club else None,
        logo_club=db_usuario.logo_club if db_usuario.logo_club else None,
        nombre_promotora=db_usuario.nombre_promotora if db_usuario.nombre_promotora else None,
        logo_promotora=db_usuario.logo_promotora if db_usuario.logo_promotora else None,
        comunidad=db_usuario.comunidad if db_usuario.comunidad else None,
        provincia=db_usuario.provincia if db_usuario.provincia else None,
        telefono1=db_usuario.telefono1 if db_usuario.telefono1 else None,
        telefono2=db_usuario.telefono2 if db_usuario.telefono2 else None,
        telefono3=db_usuario.telefono3 if db_usuario.telefono3 else None,
        foto_perfil=db_usuario.foto_perfil if db_usuario.foto_perfil else None,
        fecha_creacion=fecha_creacion_formateada  # Formateamos la fecha
    )


#........BOXEADORES

# Modelo de SQLAlchemy para Boxeadores
class Boxeador(Base):
    __tablename__ = 'boxeadores'

    Id_boxeador = Column(Integer, primary_key=True, index=True)
    nombre = Column(String(100), nullable=False)
    apellido = Column(String(100), nullable=False)
    fecha_nacimiento = Column(Date, nullable=False)
    dni_boxeador = Column(String(20), nullable=False, unique=True)
    genero = Column(Boolean, nullable=False)
    peso = Column(Float, nullable=False)  # ← Cambiado de String a Float
    categoria = Column(String(50), nullable=False)
    foto_perfil = Column(String(1000000), nullable=True)
    comunidad = Column(String(100), nullable=True)
    provincia = Column(String(100), nullable=True)
    club_id = Column(Integer, ForeignKey('usuarios_app.id_usuario'))  # Relación con el club
    fecha_registro = Column(TIMESTAMP, server_default='CURRENT_TIMESTAMP')

    club = relationship("UsuarioApp", back_populates="boxeadores")


# Modelo Pydantic para la creación de un boxeador
class BoxeadorCreate(BaseModel):
    nombre: str
    apellido: str
    fecha_nacimiento: date
    dni_boxeador: str
    genero: bool
    peso: float
    categoria: str
    comunidad: Optional[str]
    provincia: Optional[str]
    club_id: int
    foto_perfil: Optional[str] = None

    class Config:
        from_attributes = True


# Modelo Pydantic para la respuesta (GET)
class BoxeadorOut(BaseModel):
    Id_boxeador: int
    nombre: str
    apellido: str
    fecha_nacimiento: date
    dni_boxeador: str
    genero: bool
    genero = Column(Boolean, nullable=False)
    peso: float
    categoria: str
    comunidad: Optional[str]
    provincia: Optional[str]
    club_id: int
    foto_perfil: Optional[str]
    fecha_registro: Optional[datetime]

    class Config:
        from_attributes = True


# # Método para crear boxeador
@api_teambox.post("/Boxeadores/Crear")
def create_boxeador(boxeador: BoxeadorCreate, db: Session = Depends(get_db)):
    # Validar que el club existe
    club = db.query(UsuarioApp).filter(UsuarioApp.id_usuario == boxeador.club_id).first()
    if not club:
        raise HTTPException(status_code=400, detail="El club indicado no existe")

    nuevo_boxeador = Boxeador(
        nombre=boxeador.nombre,
        apellido=boxeador.apellido,
        fecha_nacimiento=boxeador.fecha_nacimiento,
        dni_boxeador=boxeador.dni_boxeador,
        genero=boxeador.genero,
        peso=boxeador.peso,
        categoria=boxeador.categoria,
        comunidad=boxeador.comunidad,
        provincia=boxeador.provincia,
        club_id=boxeador.club_id,
        foto_perfil=boxeador.foto_perfil
    )

    try:
        db.add(nuevo_boxeador)
        db.commit()
        db.refresh(nuevo_boxeador)
        return {"mensaje": "Boxeador creado correctamente", "id": nuevo_boxeador.Id_boxeador}
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=400, detail="Error al crear el boxeador: " + str(e))


# Método para editar boxeador
@api_teambox.put("/Boxeadores/Editar/{id}")
def edit_boxeador(id: int, boxeador: BoxeadorCreate, db: Session = Depends(get_db)):
    db_boxeador = db.query(Boxeador).filter(Boxeador.Id_boxeador == id).first()

    if not db_boxeador:
        raise HTTPException(status_code=404, detail="Boxeador no encontrado")

    # Validar que el club existe
    club = db.query(UsuarioApp).filter(UsuarioApp.id_usuario == boxeador.club_id).first()
    if not club:
        raise HTTPException(status_code=400, detail="El club indicado no existe")

    db_boxeador.nombre = boxeador.nombre
    db_boxeador.apellido = boxeador.apellido
    db_boxeador.fecha_nacimiento = boxeador.fecha_nacimiento
    db_boxeador.dni_boxeador = boxeador.dni_boxeador
    db_boxeador.genero = boxeador.genero
    db_boxeador.peso = boxeador.peso
    db_boxeador.categoria = boxeador.categoria
    db_boxeador.comunidad = boxeador.comunidad
    db_boxeador.provincia = boxeador.provincia
    db_boxeador.club_id = boxeador.club_id
    db_boxeador.foto_perfil = boxeador.foto_perfil

    try:
        db.commit()
        db.refresh(db_boxeador)
        return {"mensaje": "Boxeador actualizado correctamente"}
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=400, detail="Error al editar el boxeador: " + str(e))


# Método para eliminar un boxeador
@api_teambox.delete("/Boxeadores/Eliminar/{id}")
def delete_boxeador(id: int, db: Session = Depends(get_db)):
    db_boxeador = db.query(Boxeador).filter(Boxeador.Id_boxeador == id).first()

    if not db_boxeador:
        raise HTTPException(status_code=404, detail="Boxeador no encontrado")

    try:
        db.delete(db_boxeador)
        db.commit()
        return {"mensaje": "Boxeador eliminado correctamente"}
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=400, detail="Error al eliminar el boxeador: " + str(e))


@api_teambox.get("/Boxeadores/Club/{clubId}", response_model=List[BoxeadorOut])
def obtener_boxeadores_por_club(clubId: int, db: Session = Depends(get_db)):
    boxeadores = db.query(Boxeador).filter(Boxeador.club_id == clubId).all()

    if not boxeadores:
        raise HTTPException(status_code=404, detail="No se encontraron boxeadores para este club")

    return boxeadores

    
    
# Metodo para ver si DNI existe en BBDD
@api_teambox.get("/Boxeadores/dniExiste", response_model=bool)
def dni_existe(dni_boxeador: str, db: Session = Depends(get_db)):
    try:
        existe = db.query(Boxeador).filter(Boxeador.dni_boxeador == dni_boxeador).first()
        return existe is not None
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error interno: {str(e)}")
        
        

        
#Realizar la búsqueda de los boxeadores en BBDD con filtros       

# Modelo Pydantic para los filtros de búsqueda de boxeadores
class FiltrosBusquedaBoxeador(BaseModel):
    nombre_o_apellido: Optional[str] = None   
    peso_min: Optional[float] = None           
    peso_max: Optional[float] = None           
    comunidades: Optional[List[str]] = None   
    categorias: Optional[List[str]] = None     
    genero: Optional[bool] = None               
    nombre_club: Optional[str] = None           

@api_teambox.post("/Boxeadores/Busqueda")
def buscar_boxeadores(filtros: FiltrosBusquedaBoxeador, db: Session = Depends(get_db)):

    query = db.query(Boxeador).join(UsuarioApp, Boxeador.club_id == UsuarioApp.id_usuario)

    # Filtrar por nombre o apellido (boxeador)
    if filtros.nombre_o_apellido:
        texto = f"%{filtros.nombre_o_apellido.lower()}%"
        query = query.filter(
            (Boxeador.nombre.ilike(texto)) | 
            (Boxeador.apellido.ilike(texto))
        )

    # Filtrar rango de peso
    if filtros.peso_min is not None:
        query = query.filter(Boxeador.peso >= filtros.peso_min)
    if filtros.peso_max is not None:
        query = query.filter(Boxeador.peso <= filtros.peso_max)

    # Filtrar comunidades (lista)
    if filtros.comunidades:
        query = query.filter(Boxeador.comunidad.in_(filtros.comunidades))

    # Filtrar categorías (lista)
    if filtros.categorias:
        query = query.filter(Boxeador.categoria.in_(filtros.categorias))

    # Filtrar género (boolean)
    if filtros.genero is not None:
        query = query.filter(Boxeador.genero == filtros.genero)

    # Filtrar por nombre del club (parcial)
    if filtros.nombre_club:
        texto_club = f"%{filtros.nombre_club.lower()}%"
        query = query.filter(UsuarioApp.nombre_club.ilike(texto_club))

    resultados = query.all()

    # Retornar la lista de boxeadores como diccionarios
    return [
        {
            "Id_boxeador": b.Id_boxeador,
            "nombre": b.nombre,
            "apellido": b.apellido,
            "fecha_nacimiento": b.fecha_nacimiento.strftime("%Y-%m-%d"),
            "dni_boxeador": b.dni_boxeador,
            "genero": b.genero,
            "peso": b.peso,
            "categoria": b.categoria,
            "comunidad": b.comunidad,
            "provincia": b.provincia,
            "club_id": b.club_id,
            "foto_perfil": b.foto_perfil,
            "fecha_registro": b.fecha_registro.strftime("%Y-%m-%d %H:%M:%S") if b.fecha_registro else None,
        }
        for b in resultados
    ]