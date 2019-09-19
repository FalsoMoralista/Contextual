#include <stdlib.h>
#include <stdio.h>

typedef struct Node{
    void *data;
    struct Node *next;
}Node;


typedef struct List{
    int size;
    struct Node *head;  
}List;


void AddElement(Node *n, List *l){
    if (l->size==0){      
        l->head = n;
        l->head->next = NULL; 
        l->size += 1;
    }else {
        Node *aux = (Node*)malloc(sizeof(Node));        
        Node *ant;
        ant = l->head;
        aux = l->head->next;
        while(aux != NULL){
            ant = aux;
            aux = aux->next;        
        }
        n->next = NULL;
        ant->next = n;
        l->size += 1;
    }    
}

static List *NewList(){
    List *l = (List*)malloc(sizeof(List));
    l->head = NULL;
    l->size = 0;
    return l;
}

void print(List *l){
        Node *aux = (Node*)malloc(sizeof(Node));        
        Node *ant;
        aux = l->head;
        while(aux != NULL){
            printf("%lf\n",*(double*)aux->data);
            ant = aux;
            aux = aux->next;        
        }
}

int main(int argc, char const *argv[])
{
    List *l = NewList();
    for (int ctr = 0; ctr < 5; ctr++) {
        Node *n = (Node*)malloc(sizeof(Node));
        double d = 1.3352 + ctr;
        n->data = &d;
        AddElement(n,l);   
    }
    print(l);
    printf("Tamanho: %d\n",l->size);
    return 0;
}

