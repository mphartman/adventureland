# Running in AWS

## AWS Elastic Beanstalk

### Configuration Options

[Full reference](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/command-options.html)

#### Configuration Files (.ebextensions)

The `.ebextensions` folder is included in the application source bundle 
and contains configuration files

* folder at top level of project
* contains files with .config extension
* have lowest precedence

[Full reference](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/ebextensions.html)

#### Saved Configurations

The `.elasticbeanstalk` folder is used by the EB CLI.

Saved configurations belong to an application and can be 
applied to new or existing environments for that application.

Upload a saved configuration to S3:

    eb config put adventureland-api-v1.cfg.yml

Apply a saved configuration to create an environment:

    eb create --cfg adventureland-api-v1
    
If the EB CLI does not find the configuration locally, 
it looks in the Elastic Beanstalk storage location in Amazon S3

Apply a saved configuration to a running environment:

    eb config --cfg adventureland-api-v1
