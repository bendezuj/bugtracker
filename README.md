# bugtracker
Start Docker: 
docker run -it --rm bugtracker OR docker run -it -p 8080:8080 bugtracker

**Rebuild Docker (if changes applied):**
mvn clean package 
docker build -t bugtracker
docker run -it --rm bugtracker

You will see the following options:

Bug Tracker CLI
1. Create Issue
2. Close Issue
3. Exit

Issues will be saves in an xlsx file named issue_file.xlsx

