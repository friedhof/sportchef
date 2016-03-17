*SportChef*
===========

[![Build Status](https://travis-ci.org/McPringle/sportchef.svg?branch=develop)](https://travis-ci.org/McPringle/sportchef) [![Coverage Status](https://coveralls.io/repos/github/McPringle/sportchef/badge.svg?branch=develop)](https://coveralls.io/github/McPringle/sportchef?branch=develop) [![Codacy Badge](https://api.codacy.com/project/badge/grade/c9339611012742f68b0d8e0f0dcd2064)](https://www.codacy.com/app/McPringle/sportchef) [![Dependency Status](https://www.versioneye.com/user/projects/562e699c36d0ab001600160c/badge.svg?style=flat)](https://www.versioneye.com/user/projects/562e699c36d0ab001600160c) [![Dependency Status](https://www.versioneye.com/user/projects/562e699636d0ab00210013b8/badge.svg?style=flat)](https://www.versioneye.com/user/projects/562e699636d0ab00210013b8)<br/>[![Stories in Ready](https://badge.waffle.io/McPringle/sportchef.png?label=ready&title=Ready)](http://waffle.io/McPringle/sportchef) [![Issue Stats](http://issuestats.com/github/McPringle/sportchef/badge/issue)](http://issuestats.com/github/McPringle/sportchef) [![Issue Stats](http://issuestats.com/github/McPringle/sportchef/badge/pr)](http://issuestats.com/github/McPringle/sportchef) [![Join the chat at https://gitter.im/McPringle/sportchef](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/McPringle/sportchef?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)<br/>[![](https://badge.imagelayers.io/mcpringle/sportchef:latest.svg)](https://imagelayers.io/?images=mcpringle/sportchef:latest)

**Sports Competition Management Software with a modern and fast architecture. Java based backend with a RESTful JSON API and a HTML 5 client with a modern UI.**

*Copyright (C) 2015 Marcus Fihlon*

This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.

# Running *SportChef*

## Using Docker

The *SportChef* docker image is available on DockerHub. To run *SportChef*, you have to specify a port mapping to map the ports of the application server inside the container (8080) to a port on your machine (e.g. 80) and you have to specify a folder on your machine to store the permanent data. The complete docker call looks like this:

`docker run -p [local port]:8080 -v [local data path]:/opt/jboss/.sportchef -it mcpringle/sportchef`

Example:

`docker run -p 80:8080 -v /home/mcpringle/.sportchef:/opt/jboss/.sportchef -it mcpringle/sportchef`

## Using Application Server

If you have an own application server running, you can deploy the *SportChef* archive `sportchef.war`.

# Release Notes

## Version 1.1.1

- Bugfix for infrequent `NullPointerException` after creating a new event

## Version 1.1

- Default images are assigned to events without an image
- The imprint now opens in a dialog instead of a separate page
- Started with integration builds
- Added a lot of automated tests
- Measure the test coverage
- Static code analysis to increase code quality
- Fixed a lot of small bugs
- Increased code quality

## Version 1.0

- List all events on one page
- Use persistence framework (no database)
- Add imprint page
- New administration page
- Custom configuration file
- Resize event images on upload
- Calculate background color of events based on the event image

# Technology

## Server

The saver is base on Java EE technology and provides high-performance, RESTful web services. Everything is tied together using a [Maven](https://maven.apache.org/) build.

## Client

For the client we decided wo go with [HTML5](http://www.w3.org/TR/html5/) and [Google Polymer](https://www.polymer-project.org/).

## Website

We'll use [GitHub Pages](https://pages.github.com/) for hosting our upcoming project website. We only need static content, so we decided to go with [Hugo](http://gohugo.io/), a fast and modern static website engine. Our content will be written in [Markdown](http://en.wikipedia.org/wiki/Markdown) syntax.

## Tools

We use a [Maven](https://maven.apache.org/) build to tie everything together. As a result this project is IDE independent (every state-of-the-art IDE should be able to import and/or use a Maven project). [VersionEye](https://www.versioneye.com/user/projects/55715899626264001e000000) is used to track out of date dependencies. To track our reaction time on issues and pull requests we use [Issue Stats](http://issuestats.com/github/McPringle/sportchef).

# How to contribute to *SportChef*

## Contributors

A huge thank you to all the contributors! All contributors are listed below (sorted alphabetically by GitHub username):

- [CoalaJoe](https://github.com/CoalaJoe)
- [Interactiondesigner](https://github.com/Interactiondesigner)
- [jarekratajski](https://github.com/jarekratajski)
- [McPringle](https://github.com/McPringle)
- [PReimers](https://github.com/PReimers)

If you are a contributor and you are missing on this list, please add your entry yourself and create a pull request or create an [issue](https://github.com/McPringle/sportchef/issues).

## Milestones

| Version | Planned Release Date  | Status           |
| ------- | --------------------- | ---------------- |
|    v1.0 | End of January 2016   | released         |
|    v1.1 | End of February 2016  | released         |
|    v1.2 | End of March 2016     | work in progress |
|    v2.0 | End of April 2016     |                  |
|    v2.1 | End of May 2016       |                  |
|    v2.2 | End of June 2016      |                  |
|    v3.0 | End of July 2016      |                  |
|    v3.1 | End of August 2016    |                  |
|    v3.2 | End of September 2016 |                  |
|    v4.0 | End of October 2016   |                  |
|    v4.1 | End of November 2016  |                  |
|    v4.2 | End of December 2016  |                  |

## Overview of issues

We use [Waffle.io](http://waffle.io/McPringle/sportchef) for a really useful overview of our issues. Our issues can have one of five states. The states are:

| Status      | Explanation                                                                        |
| ----------- | ---------------------------------------------------------------------------------- |
| Backlog     | All open issues *not ready* to be worked on.                                       |
| Ready       | All open issues *ready* to be worked on.                                           |
| In Progress | All issues, somebody *is working* on.                                              |
| In Review   | Implementation is finished but should be reviewed or is waiting as a pull request. |
| Done        | Issues closed in the last week.                                                    |

**If you would like to contribute to *SportChef*, please choose an issue from the *Ready* column and add a comment, so that we know what issue you choosed and we're able to update the status of the issue.**

If you prefer an issue which is not yet ready and listed in the *Backlog*, please add a comment before starting to work on it, so we can discuss the issue beforehand and take it to the *Ready* state.

If there is an issue already *In Progress* and you would like to help, please add a comment to the issue to get in contact with the assigned developer.

## Source code management

### GIT Workflow Rules

We use the GitFlow workflow for *SportChef*. You can read a really good explanation of GitFlow on the Blog of [Vincent Driessen](http://nvie.com/): [A successful Git branching model](http://nvie.com/posts/a-successful-git-branching-model/)

![GitFlow Workflow Visualization by Seibert Media](https://blog.seibert-media.net/wp-content/uploads/2014/03/Gitflow-Workflow-4.png)

**Important: If you start to work on a new feature, please start on the *develop* branch!**

### Keep your fork in sync

If you fork this repository, GitHub will not keep your fork in sync with this repository. You have to do it on your own.

1. If not already done, add this repository as an upstream to your repository:<br/>`git remote add upstream https://github.com/McPringle/sportchef.git`
2. Verify that this repository was added successfully:<br/>`git remote -v`
3. Fetch branches and commits from this repository to your local repository:<br/>`git fetch upstream`
4. If you are not on your local develop branch, check it out:<br/>`git checkout develop`
5. Merge the changes from this repositories develop branch into your repository):<br/>`git merge upstream/develop`
7. Push your updated repository to your GitHub fork:<br/>`git push origin develop`

If you want to merge changes from a different branch (e.g. `master`, `release`, etc), simply replace the branch name `develop` in the above command line examples with the branch name, you want to merge. 

### Frequently Asked Questions

1. When I try to push, I get a `non-fast-forward updates were rejected` error.<br/>*Your local copy of a repository is out of sync with, or behind the upstream repository, you are pushing to. You must retrieve the upstream changes, before you are able to push your local changes.*
