# README #

This README would normally document whatever steps are necessary to get your application up and running.

### What is this repository for? ###

* Quick summary
* Version
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

### How do I get set up? ###

I updated the script from our last project to work for this one.  This will work on command line, as long as you have Bash installed.  This includes linprog.


```
#!/bin/bash

repo=https://bitbucket.org/benclarktech/fsu-sabotage

projectname=fsu-sabotage

if [ -d $projectname ]; then
        echo "Are you sure you want to remove this folder?"
        read input
        if [[ $input -eq 'y' || $input -eq 'Y' || $input -eq "yes" || $input -eq "Yes" || $input -eq "YES" ]]; then
                rm -r -f $projectname
                git clone -v --progress $repo
        fi
else
        git clone -v --progress $repo
fi
```


### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact