# My Oral Village

#### Q1: What are you planning to build? 

 * Hundreds of thousands of people around the world are illiterate and/or innumerate. These skills are particularly important when it comes to handling money.

 * An inadequate understanding of any kind of monetary transaction may have severe consequences. Money may be lost, unnecessary liability may be undertaken, or a swindle may be hatched. These consequences are particularly pronounced in places like the developing world, where money is scarce, and the law is not always enforced.

 * We will be developing an Android App for My Oral Village to combat this problem, and enable financial independence in illiterate and innumerate individuals. We aim to build the app with the following four primary features, listed in order of priority.

 * To avoid the above consequences (or anything of the like), it is important that transactions take place in an environment that is safe for all parties. People must know to whom they are sending money, and exactly how much they are sending. This is why the first feature we will implement is the sending and receiving money. This feature will serve as the backbone for any transaction that takes place within the app.

 * While sending money between family and friends is helpful, many transactions take place in a business context. In this case, the parties may not have any personal relationship and it’s crucial that the app helps build trust and security so both parties are comfortable using it. We will implement a *scan and pay* feature which allows businesses and individuals to present an easily scannable QR code, that they can use to safely transfer funds.

 * Bookkeeping is important in any financial environment, which is why we will implement transaction history as our third feature. This will provide users with a way to keep track of incoming and outgoing funds – a task previously difficult for someone who is illiterate or innumerate. The user need not understand writing, numbers, or bookkeeping concepts because the app will automatically keep track of transaction amounts and destinations (people or businesses), as they occur in real time.

* Once a user learns how to use the app, the process of sending money should become easier over time. However, this process can be streamlined by including an address book so we can keep a record of people we interact with often. This way sending and requesting from friends and family can be at just a push of a button.


#### Q2: Who are your target users?
Our target users are Brett Matthews, Paul Jackson, and Floyd Harriet, our points of contact from My Oral Village. Our secondary target are potential clients of My Oral Village. These users will see the app being used in a demo and determine if it matches their needs. Our tertiary target will be the intended primary users of our app, those who have varying levels of literacy and numeracy, from illiterate and innumerate to those who are experts. 

Our product is a demo and should be easily usable for all of our target users. If we were making the final product our target users would be innumerate people who need to handle money digitally and securely. Our secondary target would be semi-innumerate people with the same need. Our tertiary would be people who wish to handle money directly with our other two targets. But as our product is a demo, a proof of concept, that means we are making a product for My Oral Village Inc. We make it to their specifications true but also they are the ones who will actually benefit from it. They will be using our app to showcase their design decisions to potential stakeholders to then commision the full app.

That said it is important for a good demo to be representative of the final product. As such our final target user would be innumerate people who need to handle money digitally. Imagine Zahra, she was born in a small village of twenty five to a farming family in a third world country. She understands a good harvest or a bad harvest by the size of the yield but she would not be able to calculate how much they could sell it for. Now forty years later she is a mother of three. She spends her time split as a housewife and a part time job to supplement her family’s income. The problem is, in this modern age, some employers want to pay digitally. Zahra also need to be able to use the money she is paid. That is where our finalized app would come in. Zahra is semi-numerate. She can handle money based on how it looks but she mixes up the different numbers easily. She would be one of our target users.

#### Q3: Why would your users choose your product? What are they using today to solve their problem/need? 


 *  Our app reduces the need for numeracy in monetary transactions. Individuals who cannot read or who cannot write will be able to pay and receive money using simple and easy to understand images and mechanics.

 * Zahra, an illiterate and semi-innumerate mother of 3 who lives in a third world country, and spends her whole day cooking, cleaning, and doing part time work to provide for her family will not be capable of using a standard financial app. However, she still needs to get paid, make purchases, and perform everyday transactions. Our app will have a small learning curve, but will feature many simple methods that will allow Zahra to simply pick up the app and perform these day-to-day transactions without any effort on her part.
 
 * One of the most important aspects of our app is the way monetary values are illustrated. Numbers and words don't mean anything to someone who is innumerate and illiterate. Numbers and words fail to properly depict the value of products and services to such a person. Our app will show monetary values in a way that an illiterate and innumerate person will understand, and therefore allow them to get a real “sense” as to what the things they are paying for are worth. It will allow them to understand the economy of basic everyday transactions and expenses in a way that might have not been possible before.

 * WeChat Pay is a similar service app that performs many similar functionalities that we are incorporating in our software. It provides the ability to pay and get paid on the spot, gives you access to your transactions history, and to maintain a list of contacts that you can send and receive money from. However, WeChat Pay comes with a learning curve that is targeted at people who are literate and have a strong understanding of everyday monetary transactions. Our target users will not be able to overcome this learning curve, nor will they be able to perform simple transactions given that WeChat Pay uses the standard language and symbols of the financial world. Our project is focused on removing this obstacle and substantially reducing the learning curve required for using the app. 


#### Q4: How will you build it? 

The application will be built with the Android Native SDK. Depending on if the client wants to keep everything internally or not, we have a few options for the backend. The first option is using Google’s Firebase to handle all the backend functions, the drawback of it is that there would be added cost for the client if they decide to deploy the application on a large scale. The other option is building our own backend, using either Node.Js or Python Flask, and using either MySQL or MongoDB for the database. Testing wise we can use Mockito and Junit on the Android side of things, if we use Flask we can use the Python unittest framework. For push notifications we can use Firebase Cloud Messaging, which can work with all of our backends and is free.

----

### Highlights

 * During our initial meetings, we discussed the possibility of building our app in the form of a progressive web app (PWA). We thought that a PWA would suit our needs perfectly, because it is both cross-platform and lightweight. As we moved forward, we first realized that we only needed an Android app. It did not make sense to (potentially) sacrifice quality for cross-platform benefits when our requirements were for a native app.

 * When first presented with My Oral Village’s product plan, we were under the impression that a significant portion of our development time would be devoted to creating a complex backend system to support financial transactions. Consequently, we briefly discussed the possibility of using cryptography and payment processing platforms. After our first meeting with My Oral Village we learned that the product was meant to be a demo and would only have to support fake transactions. While a complex backend infrastructure will have to be developed for this product – which is why we had that idea in the first place – it is not in this project’s scope.

 * We had initially planned to meet once a week as a group to discuss the progress we each made that week. After our first meeting with My Oral Village, it became clear that they would like to be more involved in the development process of this product. Consequently, we decided to add one more weekly meeting to our agenda, which would include the MOV team.


