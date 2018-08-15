grammar Adventure;

fragment LOWERCASE : [a-z] ;
fragment UPPERCASE : [A-Z] ;

file: header line+;
header: line;

line: entry (',' entry)* NEWLINE ;

entry: TEXT
     |
     ;

TEXT : ~[,\n\r"]+ ;

WORD
    : (LOWERCASE | UPPERCASE | '_')+
    ;

WHITESPACE
    : (' ' | '\t')
    ;

NEWLINE
    : ('\r'? '\n' | '\r')+
    ;