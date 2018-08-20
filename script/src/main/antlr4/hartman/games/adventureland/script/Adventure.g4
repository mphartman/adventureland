grammar Adventure;

//
// Keywords
//

ROOM            : 'room';
EXIT            : 'exit';
ITEM            : 'item';
AT              : 'at';
IN              : 'in';
INVENTORY       : 'inventory';
NOWHERE         : 'nowhere';
CALLED          : 'called';
ACTION          : 'action';
WHEN            : 'when';
AND             : 'and';
NOT             : 'not' | '!';
THEN            : 'then';
OCCURS          : 'occurs';
NORTH           : 'north';
SOUTH           : 'south';
EAST            : 'east';
WEST            : 'west';
UP              : 'up';
DOWN            : 'down';
START           : 'start';
VERBGROUP       : 'verbgroup';
NOUNGROUP       : 'noungroup';
ANY             : 'any';
NONE            : 'none';
UNKNOWN         : 'unknown';
CARRYING        : 'carrying';
PRINT           : 'print';
LOOK            : 'look';

Number
    :   [0-9]+
    ;

Identifier
	:	Letter LetterOrDigit*
	;

fragment
LetterOrDigit
    : Letter
    | [0-9]
    ;

fragment
Letter
    : [a-zA-Z$_] // these are the "java letters" below 0x7F
    | ~[\u0000-\u007F\uD800-\uDBFF] // covers all characters above 0x7F which are not a surrogate
    | [\uD800-\uDBFF] [\uDC00-\uDFFF] // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    ;

StringLiteral
	:	'"' StringCharacters '"'
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

adventure
    :   globalParameter* gameElement+ EOF
    ;

gameElement
    :   roomDeclaration
    |   itemDeclaration
    |   vocabularyDeclaration
    |   actionDeclaration
    |   occursDeclaration
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
    :   (AT | IN)   roomName        #itemInRoom
    |   NOWHERE                     #itemIsNowhere
    |   INVENTORY                   #itemIsInInventory
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

vocabularyDeclaration
    :   verbGroup
    |   nounGroup
    ;

verbGroup
    :   VERBGROUP verb=word (',' synonym)*
    ;

nounGroup
    :   NOUNGROUP noun=word (',' synonym)*
    ;

word
    :   (Identifier | StringLiteral)
    ;

synonym
    :   Identifier
    ;

actionDeclaration
    :   ACTION actionCommand actionConditionDeclaration* actionResultDeclaration+
    ;

actionCommand
    :   actionWord+
    ;

actionWord
    : (Identifier | StringLiteral)  # actionWordWord
    | exitDirection                 # actionWordDirection
    | ANY                           # actionWordAny
    | NONE                          # actionWordNone
    | UNKNOWN                       # actionWordUnknown
    ;

actionConditionDeclaration
    :   (WHEN | AND) (NOT)? actionCondition
    ;

actionCondition
    :   (IN | AT) roomName          # conditionInRoom
    |   CARRYING itemName           # conditionItemCarried
    |   'here'  itemName            # conditionItemIsHere
    |   'present' itemName          # conditionItemIsPresent
    |   'exists' itemName           # conditionItemExists
    |   'moved' itemName            # conditionItemHasMoved
    |   'flag' word                 # conditionFlagIsTrue
    |   'counterEq' word Number     # conditionCounterEquals
    |   'counterLe' word Number     # conditionCounterLessThan
    |   'counterGt' word Number     # conditionCounterGreaterThan
    ;

actionResultDeclaration
    :   (THEN | AND) actionResult
    ;

actionResult
    :   PRINT message=StringLiteral                 # resultPrint
    |   LOOK                                        # resultLook
    |   'go'                                        # resultGo
    |   ('quit' | 'game_over' | 'gameOver')         # resultQuit
    |   'inventory'                                 # resultInventory
    |   'swap' i1=itemName i2=itemName              # resultSwap
    |   'goto' roomName                             # resultGotoRoom
    |   'put' itemName roomName                     # resultPut
    |   'putHere' itemName                          # resultPutHere
    |   'get'                                       # resultGet
    |   'drop' itemName                             # resultDrop
    |   'putWith' i1=itemName i2=itemName           # resultPutWith
    |   'destroy' itemName                          # resultDestroy
    |   'setFlag' word booleanValue                 # resultSetFlag
    |   'resetFlag' word                            # resultResetFlag
    |   'setCounter' word Number                    # resultSetCounter
    |   'incrementCounter' word                     # resultIncrementCounter
    |   'decrementCounter' word                     # resultDecrementCounter
    |   'resetCounter' word                         # resultResetCounter
    |   'setString' k=word v=word                   # resultSetString
    ;

booleanValue
    :   'true'
    |   'false'
    |   'yes'
    |   'no'
    |   'on'
    |   'off'
    |           // defaults to TRUE
    ;

occursDeclaration
    :   OCCURS (Number)? actionConditionDeclaration* actionResultDeclaration+
    ;