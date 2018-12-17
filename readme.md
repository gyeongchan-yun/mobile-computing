Breath Detects your health
===========================

UNIST CSE 2018-spring Mobile Computing Final Project
-----------------------------------------------------

Screenshots
-----------

<img width="200" src="/screenshots/start.png">&nbsp;
<img width="200" src="/screenshots/recording.png">&nbsp;
<img width="200" src="/screenshots/result.png">

Description
-----------
This project focuses on functionality rather than design. (If I get interested in UI, I'll update it later)  
User records their breath, app shows the different rate of usual breath pattern and classify faster/slower than usual.

Flow of APP Implementation
----------------------
1. Push RUN button
2. Recording
3. Store breath sound as format of .mp4 file
4. Server receives audio file
5. Audio file is converted to data by librosa (extract feature)
6. Logistic regression model determines input of breath pattern is faster or slower than usual
7. Result string responds to client
8. Result is shown on screen
