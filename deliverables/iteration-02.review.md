# My Oral Village - Team 11

## Iteration 1 - Review & Retrospect

Final review meeting.
 * When: March 10th
 * Where: UofT Campus in Bahen

#### Decisions that turned out well

- Using standard Github routine 
  - When adding new functionalities and making changes each team member created their own branch, committed their changes to that branch, and submitted a pull request. This was a crucial decision to avoid conflicts and problems during implementation.

- Using Trello to organize tasklist 
  - This allowed us to list out all of the tasks that need to be done. Team members were able to choose tasks to work on while making sure others did not work on the same thing.


#### Decisions that did not turn out as well as we hoped

- Choosing to have most of our team meeting and interactions online
  - This gave team members the flexibility to work on the project based whenever they had free time. However, at times this caused time gaps between team interactions and therefore slowed down the process.

- Trying to have weekly meetings with the client
  - Although this is good for making sure we are on the track the client envisioned not enough work was being completed week to week to justify the man hours. Thus we pushed the meetings to every other week.

#### Planned changes

- Have more in-person team meetings.
  - This is to ensure a more consistent progress. It also allows us to discuss problems and decisions with more depth, something that is hard to do online.

- Start working more consistently
  - Hand and hand with our first change is to start working more consistently. Due to various obligations we did not start coding the project until February 22nd. This causes the amount of code per commit to be higher and thus our project has less granular work.This makes rollbacks and testing harder.


## Product - Review

#### Goals and/or tasks that were met/completed:

- Sending and requesting money from contact list
- User login and verification
- Transaction History
- The data structure of our database on firebase

#### Goals and/or tasks that were planned but not met/completed:

- Allow users to check their balance
  - Currently the transactions and requests are logged but they currently have no way of determining their balance.
  - Additionally we need to allow users to accept and deny requests.
- QR code scanning functionality
  - We implemented QR scanning into a separate branch. But given that our project was deployed onto a virtual environment we were not sure how to demo this functionality. Therefore QR scanning was left out of the deployment.
  - We also plan to allow adding users to an address book using the QR code
- Address Book
  - Our current address book lets every user access every other user that is signed up for the app. We plan to build an address book that each user can personalize.

## Meeting Highlights

Going into the next iteration, our main insights are:

- Creating APIs to work from
  - Since our work involves storing data in a database, it would be very helpful to have consistent APIs to use or structure for the data. Since we are working in Firebase we sometimes have each created our individual structures for storing data.   

- Have firebase pull from git.
  - Currently team members can push changes to the firebase cloud functions at will, this can cause issues if multiple things are being worked on simultaneously, if we use the built in functionality to pull from git, we can then make sure that all changes to the firebase code are approved.



