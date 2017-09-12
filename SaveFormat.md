#Save Format

A save file will be a file with the extension ".mbf", which stands for 
"Monthly Budget File" and contents in the syntax of [JSON](http://www.json.org).

##JSON

The reader will parse the file as a JSON file. The required fields are:

- budget
- categories
- Fixed
- Variable

Yes, the 'F' and 'V' are supposed to be capitalized.

A minimal example file would look like:

```json
{
  "budget":100.0,
  "categories":["Cat1", "Cat2"],
  "Fixed":[["May 1", "Cat1", "name1", -50]],
  "Variable":[["May 2", "Cat2", "name2", -25]]
}
```

Errors may occur if not all the required fields are present in the file.

###Fields:

####budget

The amount of money for the budget. (double)

####categories

The categories available to use. (string array)

####[Fixed](#expense-list-fields)

Fixed expenses list. (array of arrays)

####[Variable](#expense-list-fields)

Variable expenses list. (array of arrays)

###Expense list fields

This includes [Fixed](#fixed) and [Variable](#variable).

An expense list contains an array of arrays. In the inner arrays, one 
consists of 4 values:

- Date string
- Category string
- Name string
- Expense double

The reader will ignore arrays with too many or little values.

