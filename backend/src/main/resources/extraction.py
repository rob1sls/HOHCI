import mysql.connector
import csv

# Connexion à la base de données
connection = mysql.connector.connect(
    host="localhost",
    user="root",
    password="handsonhcidb",
    database="my_database"
)
cursor = connection.cursor()

# Requête pour extraire les utilisateurs avec alreadyused = 1
cursor.execute("SELECT * FROM users WHERE alreadyused = 1")
users = cursor.fetchall()

# Nom du fichier CSV de sortie
csv_file_path = "/home/urbaninteraction/HOHCI/backend/src/main/resources/result.csv"

# Récupération des noms de colonnes (si nécessaire)
columns = [desc[0] for desc in cursor.description]

# Écriture dans le fichier CSV
with open(csv_file_path, mode='w', newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    
    # Écrire l'en-tête avec les noms de colonnes
    writer.writerow(columns)
    
    # Écrire les données
    writer.writerows(users)

# Fermeture de la connexion à la base de données
cursor.close()
connection.close()

print(f"Les données ont été extraites et sauvegardées dans le fichier : {csv_file_path}")
