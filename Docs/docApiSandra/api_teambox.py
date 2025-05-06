from fastapi import FastAPI, Depends, APIRouter, HTTPException, Query
from sqlalchemy import create_engine, Column, Integer, String, DateTime, ForeignKey
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session, relationship
from sqlalchemy import Date, DateTime, TIMESTAMP
from datetime import datetime
from pydantic import BaseModel
from typing import Optional, List

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

# Dependencia para obtennr la sesión de la base de datos
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
        
        
        
#........INICIO DE SESION

# Modelo Pydantic para el login

class UsuarioLogin(BaseModel):
    email: str
    contrasena: str

# Iniicio de sesion comprobando email y contraseña

@api_teambox.post("/Usuarios/Login")
def login_usuario(usuario: UsuarioLogin, db: Session = Depends(get_db)):
    db_usuario = db.query(UsuarioApp).filter(UsuarioApp.email == usuario.email).first()

    if not db_usuario:
        raise HTTPException(status_code=401, detail="Correo no registrado")

    if db_usuario.contrasena != usuario.contrasena:
        raise HTTPException(status_code=401, detail="Contraseña incorrecta")

    return {
        "id": db_usuario.id_usuario,
        "nombre": db_usuario.nombre,
        "apellido": db_usuario.apellido,
        "email": db_usuario.email,
        "es_club": bool(db_usuario.es_club),
        "es_promotor": bool(db_usuario.es_promotor),
        "nombre_club": db_usuario.nombre_club,
        "logo_club": db_usuario.logo_club,
        "nombre_promotora": db_usuario.nombre_promotora,
        "logo_promotora": db_usuario.logo_promotora,
        "comunidad": db_usuario.comunidad,
        "provincia": db_usuario.provincia,
        "telefono1": db_usuario.telefono1,
        "telefono2": db_usuario.telefono2,
        "telefono3": db_usuario.telefono3
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
    nombre_club = Column(String(100), nullable=True)
    logo_club = Column(String(1000000), nullable=True)
    nombre_promotora = Column(String(100), nullable=True)
    logo_promotora = Column(String(1000000), nullable=True)
    comunidad = Column(String(100), nullable=True)
    provincia = Column(String(100), nullable=True)
    telefono1 = Column(String(20), nullable=True)
    telefono2 = Column(String(20), nullable=True)
    telefono3 = Column(String(20), nullable=True)
    fecha_creacion = Column(TIMESTAMP, server_default='CURRENT_TIMESTAMP')
    
# Modelo Pydantic para la creación de un usuario
class UsuarioCreate(BaseModel):
    nombre: str
    apellido: str
    email: str
    contrasena: str
    es_club: bool = False
    es_promotor: bool = False
    nombre_club: Optional[str] = None
    logo_club: Optional[str] = None
    nombre_promotora: Optional[str] = None
    logo_promotora: Optional[str] = None
    comunidad: Optional[str] = None
    provincia: Optional[str] = None
    telefonos: List[str]
    
# # Método crear Usuarios
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
        nombre_club=usuario.nombre_club,
        logo_club=usuario.logo_club,
        nombre_promotora=usuario.nombre_promotora,
        logo_promotora=usuario.logo_promotora,
        comunidad=usuario.comunidad,
        provincia=usuario.provincia,
        telefono1=usuario.telefonos[0] if len(usuario.telefonos) > 0 else None,
        telefono2=usuario.telefonos[1] if len(usuario.telefonos) > 1 else None,
        telefono3=usuario.telefonos[2] if len(usuario.telefonos) > 2 else None
    )

    try:
        db.add(nuevo_usuario)
        db.commit()
        db.refresh(nuevo_usuario)  # OK en MariaDB si no usas RETURNING directamente
        return {"mensaje": "Usuario registrado correctamente", "id": nuevo_usuario.id}
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=400, detail="Error al crear el usuario: " + str(e))