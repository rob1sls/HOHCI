import csv
import mysql.connector
from datetime import datetime, timedelta
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

# Récupération des utilisateurs
cursor.execute("SELECT id, first_name, last_name FROM users")
valid_authors = cursor.fetchall()
valid_author_ids = [row[0] for row in valid_authors]
valid_names = [row[1] for row in valid_authors]
valid_fornames = [row[2] for row in valid_authors]

# Identifier les IDs des utilisateurs nommés "John"
john_ids = [row[0] for row in valid_authors if row[1] == "John"]
non_john_ids = [row[0] for row in valid_authors if row[1] != "John"]

# Chemin vers le fichier CSV
csv_file_path = "C:/Users/robin/Desktop/HOHCI/backend/src/main/resources/tweets_both.csv"


csv_file_path_good = "C:/Users/robin/Desktop/HOHCI/backend/src/main/resources/tweets_good.csv"

# Définir la plage de dates (2 ans en arrière)
start_date = datetime.now() - timedelta(days=730)  # 730 jours = 2 ans
end_date = datetime.now()

# Fonction pour générer une date aléatoire dans une plage
def random_date(start, end):
    delta = end - start
    random_days = random.randint(0, delta.days)
    random_seconds = random.randint(0, 86399)  # 86399 secondes dans une journée
    return start + timedelta(days=random_days, seconds=random_seconds)

# Lire le fichier CSV
with open(csv_file_path, newline='', encoding='utf-8') as csvfile:
    csvreader = csv.reader(csvfile, quotechar='"', skipinitialspace=True)
    next(csvreader)  # Passer l'en-tête si nécessaire

    for row in csvreader:
        try:
            content = row[0].strip()  # Contenu brut
            hateful_type = map_hateful_type(row[1])  # Type haineux
        except IndexError:
            continue 
            # Associer à "John" si le type est "NOT", sinon à un autre utilisateur
        
        user_id = random.choice(non_john_ids)


        # affiche le nom de l'utilisateur choisit
        print(valid_names[valid_author_ids.index(user_id)])
        
     
        # Générer une date aléatoire pour date_created
        date_created = random_date(start_date, end_date)

        # Ajouter quelques heures/minutes pour date_last_modified (parfois identique à date_created)
        date_last_modified = date_created + timedelta(
            hours=random.randint(0, 5),
            minutes=random.randint(0, 59)
        )

        # Valeurs aléatoires pour d'autres colonnes
        like_count = random.randint(0, 100)
        comment_count = random.randint(0, 50)
        share_count = random.randint(0, 30)
        is_type_share = 0
        reported = False
        is_reported = False

        # Insertion SQL
        sql = """
        INSERT INTO Posts (content, date_created, date_last_modified, like_count, author_id, comment_count, share_count, is_type_share, reported, hateful_type, is_reported)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """
        cursor.execute(sql, (content, date_created, date_last_modified, like_count, user_id, comment_count, share_count, is_type_share, reported, hateful_type, is_reported))
# Sauvegarder les transactions


# Lire le fichier CSV
with open(csv_file_path_good, newline='', encoding='utf-8') as csvfile:
    csvreader = csv.reader(csvfile, quotechar='"', skipinitialspace=True)
    next(csvreader)  # Passer l'en-tête si nécessaire

    for row in csvreader:
        try:
            content = row[0].strip()  # Contenu brut
            hateful_type = map_hateful_type(row[1])  # Type haineux
        except IndexError:
            continue 
            # Associer à "John" si le type est "NOT", sinon à un autre utilisateur
        user_id = random.choice(john_ids)



        # affiche le nom de l'utilisateur choisit
        print(valid_names[valid_author_ids.index(user_id)])
        
     
        # Générer une date aléatoire pour date_created
        date_created = random_date(start_date, end_date)

        # Ajouter quelques heures/minutes pour date_last_modified (parfois identique à date_created)
        date_last_modified = date_created + timedelta(
            hours=random.randint(0, 5),
            minutes=random.randint(0, 59)
        )

        # Valeurs aléatoires pour d'autres colonnes
        like_count = random.randint(0, 100)
        comment_count = random.randint(0, 50)
        share_count = random.randint(0, 30)
        is_type_share = 0
        reported = False
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
