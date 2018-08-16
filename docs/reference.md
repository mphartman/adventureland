# Adventure Script format reference manual

Or *How to write your own adventure stories.*

This is the reference manual for the Adventureland game engine's adventure story format.
With this format you can write your own [adventure game](https://en.wikipedia.org/wiki/Adventure_game) 
story and play it using the Adventureland game engine.

 

## Table of Contents

* [Overview](#overview)
* Rooms
* Items
* Vocabulary
* Actions
* [References](#references)

## Overview

An adventure story script, or script for short, is a text file written in a simple syntax which
describes the elements of the adventure world and how the player can interact with them.

### Example
        
        # adventure.txt - story script for the adventure 'Haunted House on Rural Route 1'
        
        room livingRoom "I'm in a small carpeted room with large windows looking out over a lake."
            exit east kitchen
            
        room kitchen "I'm in a cramped kitchen. The appliances look old and in disrepair."
            exit west livingRoom
            
        item microwave "a 1200-watt microwave oven"
        
        action open microwave
            when here microwave
            then print "Oh my goodness! How'd did that get in there? That's not right."
        

## References

* Scott Adams - Official Site (http://www.msadams.com/index.htm)
* An Adventure In Small Computer Game Simulation (http://mud.co.uk/richard/aaiscgs.htm)
* Mike Taylor's ScottKit Reference Manual (https://github.com/MikeTaylor/scottkit/blob/master/docs/reference.md)