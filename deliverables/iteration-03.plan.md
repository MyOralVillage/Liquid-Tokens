# My Oral Village/Team 11

## Iteration 3

 * Start date: March 4th
 * End date: March 31st

## Process

#### Changes from previous iteration

We did not make any significant changes to our process this iteration. The following is a list of the most sucesfull process decisions we made:

1. Slack

   We chose to use *Slack* as our primary mode of communication. *Slack* allows us to communicate quickly and effectively: With apps available on all devices and operating systems, we are able to respond to *Slack* messages at any time. *Slack* also allows us to integrate with third-party services. We have chosen to integrate with *Google Drive* and *Trello*. Integration with*Google Drive* allows us to seamlessly share documents and files. Integration with *Trello* allows us to reference boards and cards, which we will describe below. We have measured the effectiveness of this process decision by examining a time-to-reply  (TTR) metric (the average time it takes someone to respond to a message). We noticed that the TTR on our *Slack* channel is on par, if not higher than what we usually see with traditional forms of communication (email, text, Facebook Messenger).

2. Trello

   We chose to use *Trello* as our primary means of task delegation and organization. Using *Trello* has allowed us to excel as a team, because we are able to see exactly what needs to be done, and exactly what everyone is working on. We have divided our *Trello* board into the following lists: `User Stories`, `Planned`, `Implementing`, `Testing`, `Complete` and `Suspended`. We have found that these lists cover every single state that a task could be in. The metric we have used to examine the effectiveness of this process decision is the rate-of-task-overlap (RTO) metric. This metric measures how often two individuals overlap in their work on a specific task. A high RTO is undesireable because it waste time and leads to code conflicts. We are proud to say that the RTO for this iteration was zero (meaning our work on any tasks did not overlap with another's).

3. Github Pull Requests (PRs)

   We chose to use *Github* pull requests to manage the integration of new features into our master branch. We require there to be atleast one review on a PR (by someone other than the submitter) for  it to be merged. With PRs, we are enabled to "inspect" each other's code and feature functionality, which helps in avoiding bugs and code-smells. A metric that could be use to measure the success of this process decision is the frequency of activity on PRs. That is, comments, commits or changes to code that exist as a result of there being a PR. More activity means that a higher number of bugs, code-smells etc. were weeded about before the feature could make it to the master branch.

#### Roles & responsibilities

##### Roles Glossary

*Android:* We are developing an Android app, using the native Android SDK. In layman's terms, the native Android SDK is what developers use to create apps that run on the Android operating system.

*Localization:* Localization is the process of enabling the app to support multiple langauages, regions and currencies.

*Deployment:* Deployment is the process by which the app is delivered to our end users.

*Full Stack:* Full stack concerns both backend and frontend, which are described below.

*Frontend:* Work on the frontend concerns parts of the application that are user facing, or are directly related to some user facing feature. For example, working on a registration page constitute as work on the frontend.

*Backend:* Work on the backend concerns parts of the application that live on the server. In our case, this means the mock database, and anything to do with *Firebase*, which is a  service provided by Google.

**Bobin Chen**: Backend + Unit Testing. 

Bobin will be involved in the writing of unit tests ensuring our work is correct before it can be committed. This will help reduce issues before our changes make it to master and will prevent the client from receiving apps that do not work. He will also be partly in charge of managing the database, where and how all the data will be stored.									

- Strengths: Java (Backend), MySQL, Javascript 					

- Weaknesses: CSS, Agile Methodology, Git

**Kevin Hu:** - Full Stack + Localization. 

Kevin will take on localization of the app as, in the future, it will need to be adapted into many different languages and countries. He will ensure that other regions and languages can be easily added without changing the actual code. He is also the primary contact to My Oral Village and will communicate between the two parties. 									

- Strengths: Android (Full Stack), MySQL, Design 					

- Weaknesses: Testing

**Manu Cherthedath:** - Backend + Deployment. 

Manu is responsible for the design and implementation of the backend, in particular the database which will store the transactions, as well as managing the server the backend server will be deployed on. He will also write scripts that will allow for the app to be deployed by the client. 

- Strengths: MySQL, MongoDB, Flask, Java (Backend), Server Management 
- Weaknesses: GUI, Understanding what is user friendly for the average human.

**Matthew Tory:** - Front End + Implementation. 

Matthew will be taking involved in the visual design process of the app. He will be taking some of the mockups and designs from the client and implementing them into our app.

- Strengths: Android, Flutter (Cross Platform), Graphic Design 				
- Weaknesses: Databases

**Shervin K** - Full Stack + User Experience. 

Shervin will be involved in the process of taking our backend APIs and connecting them to the front end. He will also focus on the usability of our app ensuring all users will have a good experience.

- Strengths: Java (Full Stack), Android, MySQL, GUI, UX 				
- Weaknesses: Testing and documentation

**Simon Berest** - Front End + Research. 

Simon will be involved in researching different design philosophies to determine what will work best for our users. Using this research he will implement changes that will help reduce the learning curve for new users. 

- Strengths: Java, Python, C 								

- Weaknesses: Everything else stemming from a lack of meaningful experience

#### Team Rules

Our teams culture is mature and flexible. One of the first things that we discussed in class was how to handle a variety of problems. The overwhelming solution to the wide range of those problems was to talk about it like a mature adult. There is no point in spite. If you are angry it is important to understand why and communicate the reason. Similarly we are flexible. Many of the team members have experience working in the field or with clients. As such those without this experience will use this opportunity to develop these skills and support the team. Additionally there is not one glaring weakness. We have at least two people working on the front end and two people working on the back end.

Early on our team decided to use Slack as our primary communication method. We have everyone’s email and phone number in case of emergencies or Slack failure. We decided to have a maximum of twelve hours to respond on weekdays and twenty-four hours over the weekend. We all attend the same weekly lecture and tutorial as well.

At the moment we are using Google Meet to conduct meetings with the team at My Oral Village. In addition they want us to use their git repository and have invited us to their Slack workplace. At the moment this is still under discussion. Face to face meetings are being organized, the current plan is one every two weeks.
Group members are held accountable through the shocking method of maturity. There is no three-strike-system. THere is no punishment that a group member can hand out. If a deadline is missed our first step is to figure out why. If needed, work can be redistributed. If the group as a whole feels like someone is not meeting their deadlines they can be put on notice. If they continue to fail to meet expectations that is when a professor or teaching assistant should be called in.

The first problem we discussed is that a team member is not meeting their deadlines. The solution is, if necessary, to redistribute the work. Additionally the deadlines can be made more granular so that the group member can provide some work that can be built upon.

The second problem we discussed is when one person makes decisions on behalf of everyone without any consultation. Here the solution depends on the outcome. If their choices end up being good then it is worth following through with them despite any negativity team members may feel. Still, a discussion must be had on why they decided to work by themselves. If the design choices are bad then we can ask why they chose to go that route. Even bad design choices have redeeming qualities.

The third problem would be if someone disappeared. If a group member completely cut contact and do not come to class. Our first order of business would be to try and talk with their friends. If they are completely gone the only recourse is talk with a professor to make sure they did not drop the class without informing us. In the end even if they are not kicked out of the group they still are not getting marks for it so in essence it does not matter.

#### Events

We have, on average, one and a half meetings each week, this includes a internal meeting inside our group and a external meeting with our partner. We will set a recurring meeting to ensure our working progress. We chose to do in-person meetings rather than online meetings as we feel they are better for communication and quickly sharing ideas.
Our internal group meetings will be scheduled on Tuesday, at University of Toronto. This meeting is a combined session of coding and coding review. We will first check the progress of all our groupmates to ensure the tasks can be finished on time and rearrange the tasks if necessary. After that, we will review all our existing code and correct it if necessary. If time is available, we will code together after reviewing.
We plan to have meeting once every other week through face to face communication inside University of Toronto with our partner. The meeting is tentatively scheduled for Friday. In the meeting, we will present the current work to our partner and make amendments using the feedback from them. This meeting can be used to improve our work and make sure the functionality of our product fit the expectations of our partner.

#### Artifacts

We will employ Trello to keep track of what needs to be done. We will divide our project into small tasks that can be completed independently of each other. Tasks will then be split amongst group members. Small tasks can be completed by anyone, but larger tasks will be informally decided upon by the group. For example, we decided that Manu is going to be doing the Main work for the back end. We just do not know exactly what that work is yet.

Tasks can be in one of five states.

- **Planned** meaning the task has been created, but not yet started by any group member
- **Implementing** meaning some group member is actively working on completing the task
- **Testing** meaning the product of the task is being tested or refined
- **Completed** meaning the task has been completed
- **Suspended** the task has been suspended and/or is no longer needed

To ensure that tasks are being completed actively and efficiently, we will set task deadlines. This means that the group member assigned to a task must mark it Completed by the set deadline.
During our work, we may find that new tasks need to be created, or tasks need to be redistributed among group members. Generally speaking, we will strive to divide tasks evenly amongst group members, however this depends on the complexity of each task, availability of each member, and our individual skill sets.

When a group member has completed their assigned task(s), they are free to self-assign themselves a new task.

We prioritize the completion of tasks based on how strongly they relate to our core features (these are described below, in the *Product* section). For example, a task that exists, but has not been prioritized is the integration of a PIN system to secure accounts when the app is opened. While this feature may be useful, it is not necessary for us to complete in order to satisfy core functionality.

#### Deployment and Github Workflow

##### GitHub Workflow

1. When work on a new feature commences a branch is created for it.
2. All work is done on that branch, this allows us to avoid conflicts while a feature is still in progress.
3. When the work is ready to be merged the work is pushed, and a pull request is created (from the feature branch to master branch). The member who created the pull request then puts a message into slack requesting that another member have a look at the pull request, this is so that other members can be notified and it can be looked at quickly.
4. A different member looks at the pull request and approves or denies it. As a guideline, if code builds upon something else, the member who implemented the first feature is requested to approve it. This is so that someone who understands that part of the codebase can approve changes. 
5. If required the request is discussed on slack, and changes are made.
6. The branch is then merged into master. 

##### Deployment

For the app we build an apk within Android Studio and deploy to appetize.io for deliverables. When we are writing and testing on our own machines, we deploy directly to our phones or to an emulator using Android Studio and ADB.

For the firebase we either make changes directly in the firebase console or use firebase deploy from our computers. 

## Product

#### Goals and tasks

In the previous iteration, we made significant progress in implementing several core features. In this iteration, we will work to improve and polish these features, and also finish the core features that were not developed. The following is a list of features (in order of priority) that we will work on in this iteration: More specifically, we will be 

- We will finish implementing the scan and pay core feature.
- We will implement the contacts system core feature that will allow users to add contacts, and send money to those contacts.
- We will be changing the design of several pages to more closely match the designs that My Oral Village has provided to us. Changes include:
  - Making the transaction history page look like a tree
  - Improving the design of the transaction requests page
  - Canonicalizing the denomination selection widget
- We will continue to localize the app, ensuring that it is built in a way that supports multiple currencies and languages

At the end of this iteration, we hope that all core features will be fully functional, and the design of the app to be satisfactory to our project partner, My Oral Village.

#### Artifacts

* One of our team members developed a set of [interactive mock-ups](https://sketch.cloud/s/ADwmR/4aLxDeo/play), which has been a useful reference for when we are coding views. These mock-ups were also helpful in communicating to our partner the general "look and feel" of the app.
* We will continue to build up the front end portion of our app with a mock database in order to demonstrate how our app will work with data. At the end of this iteration, the app will be fully functional in terms of both the backend and the front end.
* Finally, we will deliver an Android app in the form of an APK. Since our clients are requesting a demo, we will not be deploying the app and it will be up to their discretion to choose how the app will be displayed.
