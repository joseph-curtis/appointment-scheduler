# An Appointment Scheduling System

### This Project Demonstrates :
- Established CRUD (Create Read Update Delete) operations in a MySQL database
- JDBC (Java Data Base Connectivity) for data source connection
- JavaFX for desktop GUI (Graphical User Interface)
- MVC (Model View Controller) and DAO (Data Access Object) design patterns

### Purpose of the Application :
- This allows easy access to appointment and customer management.
  - Users may create, edit, or remove appointments or customers as needed.
    - The scheduler checks new or edited appointments to ensure they are within
      business hours
  - The organization's database is updated of any changes made.
- Users must authenticate themselves with a login screen to ensure only
  authorized access is allowed.
  - Logs are made of each login attempt (successful or not) for compliance.
  - Language for the login screen depends on the default language selected from
    the user's computer
    - Currently supported languages: *English, French, German, Japanese*
- Three reports may be accessed to give useful information to the user:
   - - Appointment totals by type and month
   - - Schedule for each contact within the organization
   - - Customer totals by country of origin

### Description of Additional Report :
Called "Appointment Totals by Country", it is located in the top menu
 **Reports** --> **Customer totals**.  This report shows the total number of
 customers in each of four categories, based in country of origin: U.S., U.K.,
 Canada, and Other.  The user may click the "OK" button (or press the `Enter`
 key) to close the report window when finished.

### Usage *(how to run the program)* :
1. Enter username and password to log in.
2. User is greeted with popup that shows if there are any upcoming appointments.
3. Main menu has tabs to select viewing appointments or customers.
4. In the appointment screen, use radio buttons to show appointments within the
   last month, week or all.
5. Three buttons below are used to create new appointments, or edit or delete
   the selected appointment.
  a. The buttons change to create/edit/delete customers when the appropriate tab
     is selected from above.
6. Top menu bar has options to exit, or show one of three reports.
  a. "Appointment Totals" tallies up total for appointments by selected month
     and type.
  b. "Contact Schedule" shows all appointments for selected contact.
  c. "Customer Totals" tallies up customers based on the country they reside in.
      ***--this is the additional custom report.***

## Version
**`2022.08.16`**

## Authors & Contact

- **Joseph Curtis**
  - @joseph-curtis (Github) https://github.com/joseph-curtis
  - jcur175@wgu.edu (e-Mail)

## Tech Stack

**Client:**
- Java SE `17.0.1`
- JavaFX SDK `17.0.1`
- MySQL Connector/J `mysql-connector-java-8.0.25`

**Server:**
- MySQL Community Server `8.0.25`

**IDE used:**
- IntelliJ IDEA `2021.3.2` *(Community Edition)*

## Entity Relationship Diagram
The MySQL Database for the company/organization:

https://mermaid.ink/img/pako:eNp1kMGKgzAQhl8lzLm-QG7FZiFgYzGxp1yCma2hakoad1nUd68Wt7jQndMcvu-f4R-g8haBAoaDM5dgWt2R15SSFZKMPkn8QPanU86FOjKhJKHkXtVo-wa3_B9kWrSRpLlQ-_SpfLtYb_G0lCo__n-iNl_4nl-zP3ghVZKxM8uSAz9zyXOxiI2vTES7dd-jrx9LoQrOFtd1sIMWQ2ucnXsZlhANscYWNdB5tSZcNehumrn-ZudDzLroA9BP09xxB6aPXv50FdAYevyF1npXanoAjPFvNA)](https://mermaid.live/edit#pako:eNp1kMGKgzAQhl8lzLm-QG7FZiFgYzGxp1yCma2hakoad1nUd68Wt7jQndMcvu-f4R-g8haBAoaDM5dgWt2R15SSFZKMPkn8QPanU86FOjKhJKHkXtVo-wa3_B9kWrSRpLlQ-_SpfLtYb_G0lCo__n-iNl_4nl-zP3ghVZKxM8uSAz9zyXOxiI2vTES7dd-jrx9LoQrOFtd1sIMWQ2ucnXsZlhANscYWNdB5tSZcNehumrn-ZudDzLroA9BP09xxB6aPXv50FdAYevyF1npXanoAjPFvNA
