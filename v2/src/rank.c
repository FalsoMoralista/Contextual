#include "distbin.c"
#include "linked_list.c"

typedef struct Rank {
    RankEntry *rank[20000];
}Rank;


typedef struct RankEntry {   
    int id;
    double distance;
}RankEntry;

/**
* Todo: Inserir os RankEntries ordenadamente ou implementar um método de ordenaçao.
**/
Rank *NewRank(Distbin *d){
    RankEntry rank[20000] = {};
    int size = len(d) - 180;
    for (int i = 0; i < size  ; i++) {
        RankEntry r = {i,d->distances[i]};
        rank[i] = r;
    }        
}

/**
*   Returns the length for a given Distbin.
**/
int len(Distbin *d){
    return sizeof(d->distances)/8;
}
   