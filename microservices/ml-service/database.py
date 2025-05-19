from sqlalchemy import create_engine, Column, Integer, Float, DateTime, String
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from datetime import datetime
import os
from dotenv import load_dotenv

load_dotenv()

# Database connection using pymysql
DB_URL = "mysql+pymysql://amanve:Amanverma@2001@mysql.demo-basic.svc.cluster.local:3306/hospital_management"
engine = create_engine(DB_URL, pool_pre_ping=True)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()

class HeartAttackPrediction(Base):
    __tablename__ = "ml_heart_attack_predictions"  # Changed table name to be more specific

    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    timestamp = Column(DateTime, default=datetime.utcnow)
    
    # Input features
    age = Column(Integer)
    sex = Column(Integer)
    cp = Column(Integer)
    trtbps = Column(Integer)
    chol = Column(Integer)
    fbs = Column(Integer)
    restecg = Column(Integer)
    thalachh = Column(Integer)
    exng = Column(Integer)
    oldpeak = Column(Float)
    slp = Column(Integer)
    caa = Column(Integer)
    thall = Column(Integer)
    
    # Prediction results
    risk_score = Column(Float)
    risk_level = Column(String(10))

def init_db():
    # Only create the heart attack predictions table
    Base.metadata.create_all(bind=engine, tables=[HeartAttackPrediction.__table__])

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

def save_prediction(features: dict, risk_score: float, risk_level: str):
    db = next(get_db())
    prediction = HeartAttackPrediction(
        age=features['age'],
        sex=features['sex'],
        cp=features['cp'],
        trtbps=features['trtbps'],
        chol=features['chol'],
        fbs=features['fbs'],
        restecg=features['restecg'],
        thalachh=features['thalachh'],
        exng=features['exng'],
        oldpeak=features['oldpeak'],
        slp=features['slp'],
        caa=features['caa'],
        thall=features['thall'],
        risk_score=risk_score,
        risk_level=risk_level
    )
    db.add(prediction)
    db.commit()
    db.refresh(prediction)
    return prediction

def get_training_data():
    db = next(get_db())
    predictions = db.query(HeartAttackPrediction).all()
    return predictions 