#include "distbin.c"
#include "linked_list.c"

typedef struct Rank {
    RankEntry *rank[20000];
}Rank;


typedef struct RankEntry {   
    int id;
    double distance;
}RankEntry;


int comp (const void * elem1, const void * elem2) 
{
    double f = *((double*)elem1);
    double s = *((double*)elem2);
    if (f > s) return  1;
    if (f < s) return -1;
    return 0;
}

/**
*   Returns the length for a given Distbin.
**/
int len(Distbin *d){
    return sizeof(d->distances)/8;
}


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
    qsort(rank,size,sizeof(*rank),comp);
    return rank;
}

   