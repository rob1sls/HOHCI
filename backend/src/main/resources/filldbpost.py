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
    return value
    print(f"Value: {value}")
    if value.lower().strip() in ["Racist"]:
        return "RACISM"
    elif value.lower().strip() in ["sexistOrSexualContent"]:
        return "SEXISM"
    elif value.lower().strip() in ["Discrimination"]:
        return "DISCRIMINATORY"
    elif value.lower().strip() in ["Other"]:
        return "POLITICAL"  
    else:
        return "NOT"

cursor.execute("SELECT id, first_name, last_name FROM users")
valid_authors = cursor.fetchall()
valid_author_ids = [row[0] for row in valid_authors]
valid_names = [row[1] for row in valid_authors]
valid_fornames = [row[2] for row in valid_authors]

# Chemin vers le fichier CSV
csv_file_path = "src/main/resources/tweets_tests.csv"

# Lire le fichier CSV
with open(csv_file_path, newline='', encoding='utf-8') as csvfile:
    csvreader = csv.reader(csvfile, quotechar='"', skipinitialspace=True)
    next(csvreader)  # Passer l'en-tête si nécessaire
    
    for row in csvreader:
        try:
            content = row[0].strip()  # Contenu brut
            hateful_type = map_hateful_type(row[1])  # Type haineux
            print(f"Hatefultype: {hateful_type}")
        except IndexError:
            print(f"Ligne mal formatée : {row}")
            continue  # Ignore les lignes incorrectes

        if '<user>' in content:
            random_username = random.choice(valid_names) + " " + random.choice(valid_fornames)
            content = content.replace('<user>', random_username)

        # Valeurs par défaut
        date_created = datetime.now()
        date_last_modified = date_created
        like_count = random.randint(0, 100)
        comment_count = random.randint(0, 50)
        share_count = random.randint(0, 30)
        is_type_share = 0
        reported = False
        user_id = random.choice(valid_author_ids)  # Random valid author_id
        is_reported = False

        # Insertion SQL
        sql = """
        INSERT INTO Posts (content, date_created, date_last_modified, like_count, author_id, comment_count, share_count, is_type_share, reported, hateful_type, is_reported)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """
        cursor.execute(sql, (content, date_created, date_last_modified, like_count, user_id, comment_count, share_count, is_type_share, reported, hateful_type, is_reported))

# Sauvegarder les transactions
connection.commit()

# Fermer la connexion
cursor.close()
connection.close()
