INSTRUCTIONS TO TO ADD KAGGLE DATA TO CINETRACK DATABASE

#1 LAUNCH COMMAND PROMPT OR TERMINAL IF ON MACBOOK


#2 CD TO FOLDER WITH THE DATA.SQL FILE INSIDE


#3 RUN docker cp data.sql mysql-server-x370:/data.sql


#4 LOG INTO MYSQL mysql -u root -p 



#5 USE cinetrack;
  
   
   
   
   SOURCE /data.sql;
