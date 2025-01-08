import mysql.connector

# Connect to MySQL database
connection = mysql.connector.connect(
    host="localhost",
    user="root",
    password="handsonhcidb",
    database="my_database"
)
cursor = connection.cursor()

# ID of the user who should follow everyone

# Récupérer les utilisateurs avec des IDs inférieurs à 6370
cursor.execute("SELECT id FROM users WHERE id < 1000")
lower_ids = cursor.fetchall()

# Récupérer les utilisateurs avec des IDs supérieurs à 6370
cursor.execute("SELECT id FROM users WHERE id > 1000")
higher_ids = cursor.fetchall()

# Insérer toutes les relations de suivi
for lower_id in lower_ids:
    for higher_id in higher_ids:
        try:
            # Insérer la relation de suivi (follower_id suit followed_id)
            cursor.execute("INSERT INTO follow_users (follower_id, followed_id) VALUES (%s, %s)", (lower_id[0], higher_id[0]))
        except mysql.connector.IntegrityError as e:
            print(f"Error inserting follower {lower_id[0]} following {higher_id[0]}: {e}")

# Commit the transaction
connection.commit()

# Close the connection
cursor.close()
connection.close()
