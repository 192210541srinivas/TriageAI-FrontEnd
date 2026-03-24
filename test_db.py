import sqlite3
import urllib.request

try:
    conn = sqlite3.connect(r'C:\xampp\htdocs\TriageAI\triage.db')
    curs = conn.cursor()
    curs.execute('SELECT id FROM users WHERE role="doctor"')
    docs = curs.fetchall()
    
    for d in docs:
        try:
            url = f'http://127.0.0.1:8000/dashboard/priority?doctor_id={d[0]}'
            print(f"Fetching {url}")
            res = urllib.request.urlopen(url)
            print(f"  -> SUCCESS (HTTP {res.status})")
        except Exception as e:
            print(f"  -> ERROR for doctor {d[0]}: {e}")
            if hasattr(e, 'read'):
                print(e.read().decode('utf-8'))
except Exception as e:
    print(f"DB Error: {e}")
