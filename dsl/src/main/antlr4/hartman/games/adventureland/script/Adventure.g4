grammar Adventure;

//
// Keywords
//

ROOM            : 'room';
EXIT            : 'exit';
ITEM            : 'item';
AT              : 'at';
NOWHERE         : 'nowhere';
CALLED          : 'called';
ACTION          : 'action';
WHEN            : 'when';
OCCURS          : 'occurs';
NORTH           : 'north';
SOUTH           : 'south';
EAST            : 'east';
WEST            : 'west';
UP              : 'up';
DOWN            : 'down';
START           : 'start';

adventure
    :   gameElement+ globalParameter* EOF
    ;

gameElement
    :   roomDeclaration
    |   itemDeclaration
    ;

globalParameter
    :   startParameter      #globalParameterStart
    ;

roomDeclaration
    :   ROOM roomName roomDescription roomExits?
    ;

roomName
    :   Identifier
    ;

roomDescription
    :   StringLiteral
    ;

roomExits
    : roomExit (roomExit)*
    ;

roomExit
    :   EXIT exitDirection roomName?
    ;

exitDirection
    :   NORTH   #exitNorth
    |   SOUTH   #exitSouth
    |   EAST    #exitEast
    |   WEST    #exitWest
    |   UP      #exitUp
    |   DOWN    #exitDown
    ;

startParameter
    :   START roomName
    ;

itemDeclaration
    :   ITEM itemName itemDescription itemLocation? itemAliases?
    ;

itemName
    :   Identifier
    ;

itemLocation
    :   AT roomName     #itemInRoom
    |   NOWHERE         #itemIsNowhere
    ;

itemAliases
    :   CALLED itemAlias (',' itemAlias)*
    ;

itemAlias
    :   Identifier
    ;

itemDescription
    :   StringLiteral
    ;

Identifier
	:	Letter LetterOrDigit*
	;

StringLiteral
	:	'"' StringCharacters? '"'
	;

fragment
StringCharacters
	:	StringCharacter+
	;

fragment
StringCharacter
	:	~["\\\r\n]
	|	EscapeSequence
	;

fragment
EscapeSequence
	:	'\\' [btnfr"'\\]
	;

fragment LetterOrDigit
    : Letter
    | [0-9]
    ;

fragment Letter
    : [a-zA-Z$_] // these are the "java letters" below 0x7F
    | ~[\u0000-\u007F\uD800-\uDBFF] // covers all characters above 0x7F which are not a surrogate
    | [\uD800-\uDBFF] [\uDC00-\uDFFF] // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    ;

//
// Whitespace and comments
//

WHITESPACE
    :  [ \t\r\n]+ -> skip
    ;

COMMENT
    :   '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;
