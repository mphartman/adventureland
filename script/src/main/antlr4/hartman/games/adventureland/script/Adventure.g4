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

WORDGROUP       : 'wordgroup';
ANY             : 'any';
NONE            : 'none';
UNKNOWN         : 'unknown' | 'unrecognized';

CARRYING        : 'carrying';
HERE            : 'here';
PRESENT         : 'present';
EXISTS          : 'exists';
MOVED           : 'moved';
FLAG            : 'flag';
COUNTER_EQ      : 'counterEq';
COUNTER_LE      : 'counterLe';
COUNTER_GT      : 'counterGt';
HAS_EXIT        : 'hasExit' | 'has_exit';
TIMES           : 'times';

PRINT           : 'print';
LOOK            : 'look';
GO              : 'go';
QUIT            : 'quit';
GAME_OVER       : 'game_over';
SWAP            : 'swap';
GOTO            : 'goto';
PUT             : 'put';
PUT_HERE        : 'putHere' | 'put_here' ;
GET             : 'get';
DROP            : 'drop';
PUT_WITH        : 'putWith' | 'put_with';
DESTROY         : 'destroy';
SET_FLAG        : 'setFlag' | 'set_flag' ;
RESET_FLAG      : 'resetFlag' | 'reset_flag' ;
SET_COUNTER     : 'setCounter' | 'set_counter' ;
INCR_COUNTER    : 'incrementCounter' | 'increment_counter' | 'incr' | '++' ;
DECR_COUNTER    : 'decrementCounter' | 'decrement_counter' | 'decr' | '--' ;
RESET_COUNTER   : 'resetCounter' | 'reset_counter' ;
SET_STRING      : 'setString' | 'set_string' ;

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
	    {
	        setText(getText().substring(1, getText().length()-1));
	        setText(getText().replaceAll("\\\\\"", "\""));
	        setText(getText().replaceAll("\\\\n", System.getProperty("line.separator")));
	    }
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
    :   ('//' | '#') ~[\r\n]* -> skip
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
    :   wordGroup
    ;

wordGroup
    :   WORDGROUP word (',' synonym)*
    ;

word
    :   (Identifier | StringLiteral)
    ;

synonym
    :   (Identifier | StringLiteral)
    ;

actionDeclaration
    :   ACTION actionCommand actionConditionDeclaration* actionResultDeclaration+
    ;

actionCommand
    :   actionWordOrList+
    ;

actionWordOrList
    : actionWord
    | actionWordList
    ;

actionWordList
    :   '(' actionWord (',' actionWord)* ')'
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
    |   HERE  itemName              # conditionItemIsHere
    |   PRESENT itemName            # conditionItemIsPresent
    |   EXISTS itemName             # conditionItemExists
    |   MOVED itemName              # conditionItemHasMoved
    |   FLAG word                   # conditionFlagIsTrue
    |   COUNTER_EQ word Number      # conditionCounterEquals
    |   COUNTER_LE word Number      # conditionCounterLessThan
    |   COUNTER_GT word Number      # conditionCounterGreaterThan
    |   HAS_EXIT                    # conditionRoomHasExit
    |   TIMES Number                # conditionTimes
    ;

actionResultDeclaration
    :   (THEN | AND) actionResult
    ;

actionResult
    :   PRINT message=StringLiteral                 # resultPrint
    |   LOOK                                        # resultLook
    |   GO                                          # resultGo
    |   (QUIT | GAME_OVER)                          # resultQuit
    |   INVENTORY                                   # resultInventory
    |   SWAP i1=itemName i2=itemName                # resultSwap
    |   GOTO roomName                               # resultGotoRoom
    |   PUT itemName roomName                       # resultPut
    |   PUT_HERE itemName                           # resultPutHere
    |   GET                                         # resultGet
    |   DROP itemName                               # resultDrop
    |   PUT_WITH i1=itemName i2=itemName            # resultPutWith
    |   DESTROY itemName                            # resultDestroy
    |   SET_FLAG word booleanValue                  # resultSetFlag
    |   RESET_FLAG word                             # resultResetFlag
    |   SET_COUNTER word Number                     # resultSetCounter
    |   INCR_COUNTER word                           # resultIncrementCounter
    |   DECR_COUNTER word                           # resultDecrementCounter
    |   RESET_COUNTER word                          # resultResetCounter
    |   SET_STRING k=word v=word                    # resultSetString
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