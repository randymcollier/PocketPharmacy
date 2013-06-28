Pocket Pharmacy
by Randy Collier

Pocket Pharmacy allows users to access information about the most frequently used prescription drugs on the market without requiring a constant network connection.

Major Features:
1) Quick access to crucial information pertaining to each drug.
2) Search by generic names, brand names, and therapeutic classes.
3) Local database containing the most important information that can be accessed without any network connection (i.e. generic name, brand names, therapeutic class, common dosages, and drug interactions).
4) More comprehensive information containing a complete description of each drug (i.e. FDA indication, proper dosages, side effects, pharmacokinetics, molecular structure, patient counseling, picture of medication, etc.) can be accessed via a network connection.
5) Ability to favorite useful and commonly searched drugs for even quicker access.

Features to be Implemented by Due Date:
1) Local database containing 20 of the most frequently used generic prescription drugs on the market.
2) View brand names, therapeutic class, common dosages, and drug interactions for each generic drugs.
3) Search by generic names, brand names, and therapeutic classes.
4) View more comprehensive information via a network connection by accessing a URL (www.drugs.com).

Fully Completed Features:
1) Local database containing 20 of the most frequently used generic prescription drugs on the market.
2) View brand names, therapeutic class, common dosages, and drug interactions for each generic drugs.
3) Search by generic names, brand names, and therapeutic classes.
4) View more comprehensive information via a network connection by accessing a URL (www.drugs.com).
5) Ability to favorite useful and commonly searched drugs for even quicker access.

Extra Features Added:
1) User must agree to a "Legal Agreement" before using this application. Of course, the statement I have included is simply for demonstration purposes and would need to be constructed by a lawyer of some sort.

Future Plans:
1) Currently, when the database is updated, all the tables are dropped and re-created with the new structure. This causes all the favorites to be lost. I am working on a solution to save the favorites before the upgrade takes place, and then load them back in once it is complete. My initial route was to perform a SQL dump on the favorites table then execute the statement produced to load them back in, but I'm having trouble finding a way to do that through JAVA. Its not likely that the database will be updated frequently, but I would like to have that functionality for the times that it does occur. Any suggestions would be welcomed.
2) Currently, when the more comprehensive information is viewed, I am simply loading a WebView of www.drugs.com with the selected drug name inserted in the URL. I would like to display the information in the same way that the information from the database is displayed, but without having to store all that data. Ideally, I could use a web service that outputs XML or JSON regarding the information on the drug and read in the data, parse it, and display it to the users. I looked for an existing web service to accommodate my needs but failed to find one. I will have to write my own, which isn't a problem, but I found that to be out of the scope of this class.
3) I need more data! And a more efficient way of retrieving it. For the 20 drugs currently in my application, I referenced a book titled "Sigler Drug Cards" and manually created and entered the data into the database I am using. As you can imagine, it was very time consuming and would definitely not scale to a large data set. This appears to be my largest challenge moving forward.

This application requires API 14.