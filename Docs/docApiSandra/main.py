from fastapi import FastAPI
from api.api_teambox import api_teambox


app = FastAPI()

# Incluir las rutas de las APIs
app.include_router(api_teambox, prefix="/apiTeamBox", tags=["TeamBox"])

