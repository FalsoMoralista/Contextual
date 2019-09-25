#include "distbin.c"

typedef struct RankEntry {   
    int id;
    double distance;
}RankEntry;

typedef struct Rank {
    RankEntry *entries;
}Rank;


int compare_to(const void * elem1, const void * elem2) 
{
    RankEntry r1 = *(RankEntry*)elem1;
    RankEntry r2 = *(RankEntry*)elem2;
    if (r1.distance > r2.distance) return  1;
    if (r1.distance < r2.distance) return -1;
    return 0;
}

/**
*   Returns the length for a given Distbin.
**/
int len(Distbin *d){
    return sizeof(d->distances)/8;
}

/**
 * Returns a new rank for a given distbin.
 * It does encapsulate all distances and its respective indexes
 * then sort them.
 */
Rank NewRank(Distbin *d){
    RankEntry *entries = (RankEntry*)malloc(20000 * sizeof(RankEntry));
    int size = len(d) - 180;
    for (int i = 0; i < size  ; i++) {
        RankEntry r = {i,d->distances[i]};
        entries[i] = r;
    }        
    qsort(entries,size,sizeof(RankEntry),compare_to);
    Rank r = {entries};
    return r;
}
