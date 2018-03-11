[![CircleCI](https://circleci.com/gh/ifreitas/Pinky/tree/master.svg?style=shield&circle-token=Pinky)](https://circleci.com/gh/ifreitas/Pinky)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/2fb75814031a43fc94b8a9359ce00b5e)](https://www.codacy.com/app/israel.araujo.freitas/Pinky?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ifreitas/Pinky&amp;utm_campaign=Badge_Grade)

# Pinky
It is a simple asynchronous and non-blocking web interface for Program AB. Program AB is the reference implementation of the AIML 2.0 draft specification. 
AIML is a widely adopted standard for creating chat bots and mobile virtual assistants like ALICE, Mitsuku, English Tutor, The Professor, S.U.P.E.R. and many more. 
Program AB was developed by Richard Wallace (contact info@alicebot.org) and first released in January, 2013. More info about Program AB: https://code.google.com/archive/p/program-ab/. 
That name comes from an american animated television series called [Pinky](https://github.com/ifreitas/Pinky) and the [Brain](https://github.com/ifreitas/brain).

## Getting Started
### 1. Install
1.1. Download the pink-\<version\>.zip file from the latest Pinky release at [download page](https://github.com/ifreitas/Pinky/releases/latest).

1.2. Unzip the file

1.3. Edit the pink-\<version\>/conf/production.conf file (self explanatory)

### 2. Running
```shell
$ cd pinky-<version>/bin/
$ ./pinky
```
### 3. Testing
If you have curl installed, just run:

```shell
$ curl -d "text=test" -X POST http://localhost:9000/chat/sample/some_session
```
It should returns:
```js
{ "text" : "Congratulation. It works!"}
```

### 4. Deploying New Bots
Simply past your AIML 2.0 bot into pinky-\<version\>/bots dir. It does'nt require restart the server.

**The bots must follows the AIML 2.0 structure dir**. More info at [Program-AB](https://code.google.com/archive/p/program-ab/) project page.
