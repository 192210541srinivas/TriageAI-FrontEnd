import urllib.request
import json
from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker

try:
    engine = create_engine("mysql+pymysql://root:@localhost/triageai")
    SessionLocal = sessionmaker(bind=engine)
    session = SessionLocal()
    
    result = session.execute(text('SELECT id, name FROM users WHERE role="doctor"'))
    docs = result.fetchall()
    
    if len(docs) > 0:
        d = docs[0]
        url = f'http://127.0.0.1:8000/dashboard/priority?doctor_id={d[0]}'
        res = urllib.request.urlopen(url)
        data = res.read().decode('utf-8')
        
        with open('dump.json', 'w') as f:
            f.write(data)
            
        print("Dumped JSON to dump.json")
except Exception as e:
    print(f"Test Error: {e}")
