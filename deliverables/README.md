# My Oral Village Android App
# Team 11

**Link to deployed product:** TODO

## Description 
 * This app allows users, especially those who are innumerate, to send and request money using their mobile devices.
* This app is a demo so that My Oral Village Inc to showcase their innumerate-enabling designs.
 * Millions of people around the world are financially illiterate, and as the world moves towards technological financial solutions, these individuals are being left behind. This app enables financially illiterate (or semi-illiterate) users to manage their finances in an easy-to-understand environment.
 * Given that our target users are financially illiterate, it is important to have a solution that supports this disability. Our app replicates the cash based environment (i.e. bills and coins) that these people are comfortable in. For example, when sending money, instead of the user having to input numbers (which they might not understand), they input an amount in terms of bills and coins.

## Key Features
 * **Sending money**: users can send money to their contacts using our signature “drag and drop” monetary system.
* **Requesting money**: users can request money from their contacts by using our signature “drag and drop” monetary system.
* **Login/registration**: Users can log in and register to this app using their phone number. At the time of registration, users are prompted to enter their name, and add a profile photo to ensure that they can be easily identified by other users of the app. 
  * *Note:* If the user registers with a *real* phone number, on a *real* device (i.e. not an emulator) they will be sent a six digit verification code that must be inputted into the app. Otherwise, the user must log in and register with a predefined phone number and verification code pair, several of which are provided below.
* **View and edit your Profile**: Users can edit and view their personal profile in the app. Users can change their name, profile picture, or currency.
* **Transaction history**: Users can view their transaction history, which includes incoming and outgoing funds. To ensure that the idea of time and history is communicated to our illiterate users, the transaction history is presented as a tree, where the oldest transactions appear at the bottom of the tree.
* **Transaction details**: Users can view the details of a particular transaction (incoming or outgoing). Information items include: The sender/receiver of money, the currency of the transaction, and the date of the transaction.
* **View your balance**: Users can view their account balance on the transaction history page. The balance is presented in two forms: Traditional numbers, and canonicalized denominations with tallies.
* ***Scan and Pay***: Users can scan other users' QR codes and send them money directly.
* **Transaction requests**: Users can view incoming and outgoing money requests. They can see if someone is asking them for money, and they can see the status of a request they made to someone else.
* **Contacts**: Users can add and view and search their contacts within the app. Contacts can be added via phone number, or the user can scan another users' QR code.

## Instructions
 * **Logging in/signing up**
   *  Open the app and enter your phone number. A validation code will be sent to your device, and you will be prompted to enter it into the app.
     * If it is your first time using the app, you will be prompted to choose a profile picture, enter your first name, and enter your last name. To choose a profile picture, press the circular button that looks like *"+ person icon"*.
   *  *At this stage, in order to log in, you must use a predefined phone number and validation code.* See below:
   *  New user number: +1 416-555-9001 code: 123456
   *  Existing user number: +1 416-555-2000 code: 552000
 * **Sending money**
   * To send money, navigate to the **Transactions** page via the bottom navigation bar. Press the large *Send Money* button.
   * Choose the contact you would like to send money to. To select a contact, press on one of the contact cards. Press *Next.*
   * You will now choose the amount of money you would like to send. At the bottom of the screen is a list of denominations you can use. Drag a denomination into the box with a *"+"* symbol. *Note: In order to drag a denomination, you must long click, then drag. Do not attempt to drag immediately*
   * Once you have selected the amount of money you would like to send, press next and confirm your transaction.
 * **Requesting money**
   * To request money, navigate to the **Transactions** page via the bottom navigation bar. Press the large *Request Money* button.
   * To request money, follow the same steps as above.
     Navigating to the **Scan** page via the bottom navigation bar simply displays “Hello blank fragment” as this feature is still under testing.
* **Viewing and editing your profile**
  * To view your profile, navigate to the **Profile** page via the bottom navigation bar. 
  * To edit your profile, navigate to the **Profile** page via the bottom navigation bar. Press the *Edit Profile* button and a new window will appear. From within this window...
    * To change your profile photo, click on the profile image.
    * To change your name, input a new name into the text field
    * To change your preferred currency, click on the currency button
* **Transaction history**
  * To view your transaction history, navigate to the **History** page via the bottom navigation bar. 
  * Incoming transactions (i.e. money sent *to* you) appear on the left side of the tree. 
  * Outgoing transactions (i.e. money you have *sent* to other people) appear on the right side of the tree. 
  * The most recent transactions are at the top most branches. The profile image of the other party involved in the transaction will appear on the transaction "branch", along with the amount of the transaction.
* **Transaction details**
  * To view details about a particular transaction, navigate to the **History** page via the bottom navigation bar. 
  * Tap on one of the transaction circles.
  * A new window will appear, where several pieces of information are displayed:
    * The profile picture of the other party involved in the transaction
    * The amount and currency of the transaction
    * An image (the hand) indicating whether the transaction was incoming or out going
    * The date of the transaction in two forms: The first is the ISO representation. The second is an array of dots. Simply put: more black dots means that the transaction occurred more recently. If there are outlined dots, the transaction happened less recently.
    * The phone number of the other party involved in the transaction
* **View your balance**
  * To view your balance, navigate to the **History** page via the bottom navigation bar
  * Your balance is displayed in to forms at the top of the window. The first is a traditional number display. The second is in canonicalized denomination form with tallies.
* ***Scan and Pay***
  * TODO
* **Transaction Requests**
  * TODO
* **Contacts**
  * To view your contacts, navigate to the **User** page via the bottom navigation bar.
  * Tap on the *Contacts* button.
  * You will be presented with a list of your contacts, where you can perform the following actions
    * To view details about a particular contact, tap on that contact's cell in the contacts list
    * To search your contacts, tap the *search* icon in the top right of the window and type the name of the contact you wish to find
    * To add a contact, tap the *plus* button in the bottom right of the window. You will be presented with two options: The first option (the phone) allows you to add a contact by inputting their phone number. The second option (the grid) allows you to add a contact by scanning a QR code. (*Note: if you are on an emulator, the scan functionality will not work.*)
      * To add a contact by their phone number, input the contact's phone number, then press search. Once the contact appears, press the *Add* button. An example phone number is: +1 (650) 555-1234.
