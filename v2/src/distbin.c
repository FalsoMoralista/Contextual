#include "stdlib.h"
#include "stdio.h"

const unsigned short int _SIZE_ = 20180; 

typedef struct Distbin{
    double distances[_SIZE_];
    int *id;

}Distbin;

Distbin *NewDistbin(FILE *f, Distbin *d){
    d = (Distbin*)malloc(sizeof(Distbin));
    for (int line = 0; line < _SIZE_; line++) {
        fseek(f,line*8,SEEK_SET);
        char bytes[8];
        fread(&bytes, 8, 1, f);
        double value = *((double*)bytes);
        d->distances[line] = value;
    }
    return d;
}