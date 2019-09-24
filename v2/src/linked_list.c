#include <stdlib.h>
#include <stdio.h>

typedef struct Node {
    double data;
    struct Node *next;
}Node;


typedef struct List {
    int size;
    struct Node *head; 
    void (*AddElement)(double d,struct List *l); 
}List;

// static void  AddElement(double d, List *l);

static void AddElement(double d, List *l) {
    Node *n = (Node*)malloc(sizeof(Node));    
    n->data = d;
    n->next = NULL;

    if (l->size==0){      
        l->head = n;
    }else {
        Node *ant;
        Node *aux;
        ant = l->head;
        aux = l->head;
        while(aux != NULL){
            ant = aux;
            aux = aux->next;        
        }
        ant->next = n;
    }    
        l->size += 1;
}

static List *NewList() {
    List *l = (List*)malloc(sizeof(List));
    l->head = NULL;
    l->size = 0;
    l->AddElement = AddElement;
    return l;
}

void print(List *l) {
        Node *aux;        
        Node *ant;
        aux = l->head;
        while(aux != NULL){
            printf("Data = %lf\n",(double)aux->data);
            aux = aux->next;        
        }
}

void test(int argc, char const *argv[]) {
    List *l = NewList();
    for (int ctr = 0; ctr < 100; ctr++) {
        double d = 1.0342 + ctr;
        AddElement(d,l);   
    }   
    print(l);
    printf("Tamanho: %d\n",l->size);
    return 0;
}

