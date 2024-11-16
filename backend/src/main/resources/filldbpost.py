import csv
import mysql.connector
from datetime import datetime
import random


# Connexion à la base de données
connection = mysql.connector.connect(
    host="localhost",
    user="my_user",
    password="handsonhcidb",
    database="my_database"
)
cursor = connection.cursor()

# Fonction pour mapper le type de langage haineux
def map_hateful_type(value):
    if value.lower().strip() in ["offensive language"]:
        return "OFFENSIVE"
    elif value.lower().strip() in ["hate speech", "hate speech and offensive language"]:
        return "HATEFUL"
    else:
        return "NOT"

cursor.execute("SELECT id FROM users WHERE id BETWEEN 9 AND 100")
valid_author_ids = [row[0] for row in cursor.fetchall()]


# Chemin vers le fichier CSV
csv_file_path = "src/main/resources/tweets.csv"

# Lire le fichier CSV
with open(csv_file_path, newline='', encoding='utf-8') as csvfile:
    csvreader = csv.reader(csvfile, quotechar='"', skipinitialspace=True)
    next(csvreader)  # Passer l'en-tête si nécessaire
    
    for row in csvreader:
        try:
            content = row[0].strip()  # Contenu brut
            hateful_type = map_hateful_type(row[1])  # Type haineux
        except IndexError:
            print(f"Ligne mal formatée : {row}")
            continue  # Ignore les lignes incorrectes

        # Valeurs par défaut
        date_created = datetime.now()
        date_last_modified = date_created
        like_count = random.randint(0, 100)
        comment_count = random.randint(0, 50)
        share_count = random.randint(0, 30)
        is_type_share = 0
        reported = False
        user_id = random.choice(valid_author_ids)  # Random valid author_id

        # Insertion SQL
        sql = """
        INSERT INTO Posts (content, date_created, date_last_modified, like_count, author_id, comment_count, share_count, is_type_share, reported, hateful_type)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """
        cursor.execute(sql, (content, date_created, date_last_modified, like_count, user_id, comment_count, share_count, is_type_share, reported, hateful_type))

# Sauvegarder les transactions
connection.commit()

# Fermer la connexion
cursor.close()
connection.close()
