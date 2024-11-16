import mysql.connector

# Connect to MySQL database
connection = mysql.connector.connect(
    host="localhost",
    user="my_user",
    password="handsonhcidb",
    database="my_database"
)
cursor = connection.cursor()

# ID of the user who should follow everyone
follower_id = 8

# Fetch all other user IDs except for user 8
cursor.execute("SELECT id FROM users WHERE id != %s", (follower_id,))
other_user_ids = [row[0] for row in cursor.fetchall()]

# Insert follow relationships where user 8 follows all other users
for followed_id in other_user_ids:
    sql = "INSERT INTO follow_users (follower_id, followed_id) VALUES (%s, %s)"
    cursor.execute(sql, (follower_id, followed_id))

# Commit the transaction
connection.commit()

# Close the connection
cursor.close()
connection.close()
