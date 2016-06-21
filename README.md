# CompareORM
Android application that compares DB libraries.
Based on Raizlabs [AndroidDatabaseLibraryComparison](https://github.com/Raizlabs/AndroidDatabaseLibraryComparison)
A test between a few of the popular libraries running a speed test on how fast they load and save data.

## Benchmark Description

There are two benchmarks.  The Simple trial uses a flat schema for an address book so each row is composed of name, address, city, state, and phone columns.  

![Simple Address Item Schema](db_comorm(1).png "Simple Address Item Schema")

The Complex trial is hierarchical and has support for multiple address books where each address book has contacts and addresses.

![Address Book Schema](db_comorm(2).png "Address Book Schema")

## Results

These are the results for the Simple trial:

![Simple Trial](simple_test.PNG "Simple Trial")

And these are the results for the Complex trial:

![Complex Trial](complex_test.PNG "Complex Trial")
