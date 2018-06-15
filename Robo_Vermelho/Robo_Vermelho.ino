


// --- Bibliotecas Auxiliares ---
#include <AFMotor.h>         //Inclui biblioteca AF Motor
#include <SoftwareSerial.h>

#define  parado  1
#define  movendo 0

#define    esquerda 0
#define    direita   1
#define    reto     2

const int     A = 16;
const int     B = 15;
const int     C = 14;
const int     sensorAnal = A3;    // select the input pin for the potentiometer
int     velmax = 230; //velocidade


float         average = 0;
int           index = 0;
int           last_proportional;
int           integral;
int           setpoint = 3500; // como são 8 sensores, variam de 0 a 7.000, onde 3.500 é a metade ou seja, robô simétrico sobre a linha
/*int           KP = 9;   //1 byte
  float         KD = 1; //2 bytes
  int           KI = 8500; //2 bytes*/
int           KP = 9;   //1 byte     9
float         KD = 1; //2 bytes     1
int           KI = 500; //2 bytes  4000(500 ficou bom)


int          dados[10];
int           indice = 0;
static boolean pausarPID = true,
               verificar_cruzamento = true,
               girando = false;

int           QuantSensores = 8;
int           leituras_reta;
unsigned long ultimo_valor = 0;
int           cruzamento = 0;
int           direcoes[20] = {direita, esquerda, esquerda, esquerda, reto, esquerda, direita, direita, direita, reto};



// --- Seleção dos Motores ---
AF_DCMotor roda_esquerda(2); //Seleção do Motor 1
AF_DCMotor roda_direita(4); //Seleção do Motor 2
AF_DCMotor roda_centro(1); //Seleção do Motor 2

struct sensor {
  int minimo;
  int maximo;
  int proporcao;
  int valorlido;
} sensor[8];

struct Posicao {
  int linha;
  int coluna;
  int valor; // quão bom é o lugar
};

struct Carrinho {
  String nome;
  Posicao destino;
  Posicao passado[100];
  Posicao atual;
};


// --- Protótipo das Funções Auxiliares ---
void robot_forward(unsigned int esq, unsigned char dir);
void robot_backward(unsigned char porcentagem); //Função movimenta robô trás
void robot_left(unsigned char porcentagem);     //Função movimenta robô esquerda
void robot_right(unsigned char porcentagem);    //Função movimenta robô direita
void PID();
void set_motors(int left_speed, int right_speed);
void robot_stop(unsigned char porcentagem);     //Função para para o robô*/
SoftwareSerial mySerial(13, 2); // RX, TX

Carrinho hetero;
Carrinho josegay;
Posicao CAMPO = {4, 4} ; // =  Posicao(4, 4);

void seleciona_sensor(int my_sensor) {
  delay(2);
  switch (my_sensor) {
    case (0): {
        digitalWrite(A, LOW);
        digitalWrite(B, LOW);
        digitalWrite(C, LOW);
        break;
      }
    case (1): {
        digitalWrite(A, HIGH);
        digitalWrite(B, LOW);
        digitalWrite(C, LOW);
        break;
      }
    case (2): {
        digitalWrite(A, LOW);
        digitalWrite(B, HIGH);
        digitalWrite(C, LOW);
        break;
      }
    case (3): {
        digitalWrite(A, HIGH);
        digitalWrite(B, HIGH);
        digitalWrite(C, LOW);
        break;
      }
    case (4): {
        digitalWrite(A, LOW);
        digitalWrite(B, LOW);
        digitalWrite(C, HIGH);
        break;
      }
    case (5): {
        digitalWrite(A, HIGH);
        digitalWrite(B, LOW);
        digitalWrite(C, HIGH);
        break;
      }
    case (6): {
        digitalWrite(A, LOW);
        digitalWrite(B, HIGH);
        digitalWrite(C, HIGH);
        break;
      }
    case (7): {
        digitalWrite(A, HIGH);
        digitalWrite(B, HIGH);
        digitalWrite(C, HIGH);
        break;
      }
  }
}
void calibrate() {
  int sensor_aux;
  unsigned long int valor, i;
  for (i = 0; i < 8; i++) {
    sensor[i].minimo = 1000;
    sensor[i].maximo = 0;
  }
  /* Serial.println("Iniciar Calibracao");
    for (sensor_aux = 0; sensor_aux < 8; sensor_aux++) {
     seleciona_sensor(sensor_aux);
     Serial.print("Sensor ");
     Serial.println(sensor_aux);
     robot_calibracao_direita();
     for (i = 0; i < 5000; i++)      {
       valor = analogRead(sensorAnal);
       if (valor < sensor[sensor_aux].minimo) sensor[sensor_aux].minimo = valor;
       if (valor > sensor[sensor_aux].maximo) sensor[sensor_aux].maximo = valor;

     }
    }*/
}
int quant_sensores_ativos() {
  int i, total = 0;

  for (i = 0; i < 8; i++) {
    if (sensor[i].proporcao > (sensor[i].maximo * 0.6)) ++total;

  }
  return (total);
}
void le_sensor(int i) {
  seleciona_sensor(i);
  sensor[i].valorlido = analogRead(sensorAnal);
  Serial.println(sensor[i].valorlido);
}
void le_linha() {
  int i;
  for (i = 0; i < 8; i++) {
    le_sensor(i);
  }
  // for(i=0;i<8;i++){
  calcula_proporcao();
  // }
}
void calcula_proporcao() {
  int i;
  for (i = 0; i < 8; i++) {
    sensor[i].proporcao = (sensor[i].valorlido - sensor[i].minimo) * (1000 / (sensor[i].maximo - sensor[i].minimo));
    (sensor[i].proporcao > 1023 ? sensor[i].proporcao = 1023 : sensor[i].proporcao);
    (sensor[i].proporcao < 0 ? sensor[i].proporcao = 0 : sensor[i].proporcao);
  }
}
int readLine()
{
  unsigned char i, on_line = 0;
  unsigned long avg; // this is for the weighted total, which is long
  // before division
  unsigned long sum; // this is for the denominator which is <= 64000

  le_linha();//le todos os sensores

  avg = 0;
  sum = 0;

  for (i = 0; i < QuantSensores; i++) {
    int value = sensor[i].proporcao;

    // keep track of whether we see the line at all
    if (value > 20) {
      on_line = 1;
    }

    // only average in values that are above a noise threshold
    if (value > 20) {
      avg += (long)(value) * (i * 1000);
      sum += value;
    }
  }

  /*  if(!on_line)
    {
        // If it last read to the left of center, return 0.
        if(ultimo_valor < (QuantSensores-1)*1000/2)
            return 0;

        // If it last read to the right of center, return the max.
        else
            return (QuantSensores-1)*1000;

    }*/
  ultimo_valor = avg / sum;
  return ultimo_valor;
}



void setup()
{
  pinMode(A, OUTPUT);
  pinMode(B, OUTPUT);
  pinMode(C, OUTPUT);
  Serial.begin(9600);
  delay(2000);
  Serial.println("oi");
  calibrate();
  robot_stop(255);
  mySerial.begin(9600);
  delay(3000);
  pausarPID = false;
  hetero.atual = {1, 1, 0};
}
void loop()
{
  /*int           KP = 10;   //1 byte
    int           KD = 1.5; //2 bytes
    int           KI = 10000; //2 bytes*/
  byte v;


  //Codigo Bruno Szczuk
  while (enviaPosicao(hetero.atual)) {
    hetero.destino = procuraMelhorPosicao(hetero.atual, josegay.atual);
    
  }



  //Codigo antigo
  Serial.write(5000);
  if (mySerial.available()) {
    robot_stop(255);
    pausarPID = true;
    //Serial.print(indice);
    //  Serial.print(" - ");
    v = mySerial.read();
    if (v != 255) dados[indice++] = v;
    // Serial.println(dados[indice-1]);
    //robot_stop(255);
    if (indice == 5) {
      ///   Serial.println("2 Dados");
      if (dados[0] != 255) {
        if (dados[0] == parado) {
          robot_stop(255);
          pausarPID = true;
          Serial.println("Parado");
        } else {
          if (pausarPID) {
            pausarPID = false;
            velmax = dados[1];
            Serial.println("Movendo");
          }
        }


      }
      Serial.print("Estado:  ");
      Serial.println(dados[0]);
      Serial.print("Velocidade:  ");
      Serial.println(dados[1]);
      KP = dados[2];
      Serial.print("KP:  ");
      Serial.println(KP);
      KD = (float)dados[3] / 10;
      Serial.print("KD:  ");
      Serial.println(KD);
      KI = dados[4] * 100;
      Serial.print("KI:  ");
      Serial.println(KI);
      indice = 0;

    }
  }

  if (!pausarPID)  PID();

  if (verificar_cruzamento) {
    if (sobre_cruzamento()) {
      //mySerial.write(cruzamento);
      direcao_caso_cruzamento(direcoes[cruzamento]);
      cruzamento++;
      //  direcao_caso_cruzamento(esquerda);
      if (cruzamento == 10) cruzamento = 0;
    }
  } else {
    if (++leituras_reta == 15) {
      leituras_reta = 0;
      verificar_cruzamento = true;
    }

  }
  // robot_teste(240);
  // robot_direita();
}
boolean sobre_cruzamento() {
  if ((quant_sensores_ativos() > 4))return (true);
  else  return (false);
}
void direcao_caso_cruzamento(int direcao) {
  // mySerial.write(direcao);
  switch (direcao) {
    case (esquerda): {
        pausarPID = true;
        // girando=true;
        robot_stop(255);
        delay(1000);
        robot_esquerda();
        delay(1000);
        /*do{
              robot_esquerda();
          }while((estado_sensor(0,esquerda)==false) ) ;*/
        do {
          robot_esquerda();
        } while ((estado_sensor(0, esquerda) == false) && (estado_sensor(1, esquerda) == false) && (estado_sensor(2, esquerda) == false) && (estado_sensor(4, esquerda) == false)  ) ;
        /*do{
              robot_esquerda();
          }while((estado_sensor(2,esquerda)==false) ) ;
          do{
              robot_esquerda();
          }while((estado_sensor(3,esquerda)==false) ) ;*/
        robot_stop(255);
        delay(500);
        pausarPID = false;
        // girando=false;
        break;
      }
    case (direita): {
        pausarPID = true;
        robot_stop(255);
        delay(1000);
        robot_direita();
        delay(500);
        do {
          robot_direita();
        } while ((estado_sensor(7, esquerda) == false) && (estado_sensor(6, esquerda) == false) && (estado_sensor(5, esquerda) == false) && (estado_sensor(4, esquerda) == false)  ) ;

        robot_stop(255);
        delay(500);
        pausarPID = false;
        break;
      }
    case (reto): {
        verificar_cruzamento = false;
        break;
      }
  }
}
boolean estado_sensor(int i, int direcao) {
  le_sensor(i);
  if (direcao == direita) {
    if (sensor[i].valorlido > (sensor[i].maximo * 0.5) )
      return (true);
    else
      return (false);
  } else {
    if (sensor[i].valorlido > (sensor[i].maximo * 0.6) )
      return (true);
    else
      return (false);
  }

}

void PID() {
  // Obtém a posição da linha
  // Aqui não estamos interessados nos valores individuais de cada sensor
  unsigned int position = readLine();

  // O termo proporcional deve ser 0 quando estamos na linha
  int proportional = ((int)position) - 3500;
  //   Serial.println(proportional);
  // delay(500);

  // Calcula o termo derivativo (mudança) e o termo integral (soma)
  // da posição
  int derivative = proportional - last_proportional;
  integral += proportional;

  // Lembrando a ultima posição
  last_proportional = proportional;

  // Calcula a diferença entre o aranjo de potência dos dois motores
  // m1 - m2. Se for um número positivo, o robot irá virar para a
  // direita. Se for um número negativo, o robot irá virar para a esquerda
  // e a magnetude dos números determinam a agudez com que fará as curvas/giros
  //int power_difference = proportional/10 + integral/10000 + derivative*3/2;
  int power_difference = proportional / KP + integral / KI + derivative * (KD);

  // Calcula a configuração atual dos motores.  Nunca vamos configurar
  // um motor com valor negativo
  dados[1] = 240;
  if (power_difference > dados[1])
    power_difference = dados[1];
  if (power_difference < -dados[1])
    power_difference = -dados[1];
  if (power_difference < 0)
    set_motors(dados[1] + power_difference, dados[1]);
  else
    set_motors(dados[1], dados[1] - power_difference);
}

// Acionamento dos motores
void set_motors(int left_speed, int right_speed) {
  if (right_speed >= 0 || left_speed >= 0) {
    if (right_speed >= 0 && left_speed < 0)        left_speed = -left_speed;
    if (right_speed < 0 && left_speed >= 0)        right_speed = -right_speed;
    // Serial.println(left_speed);
    // Serial.println(right_speed);
    roda_direita.setSpeed(right_speed);
    roda_direita.run(FORWARD);
    roda_esquerda.setSpeed(left_speed);
    roda_esquerda.run(FORWARD);
  }
}
//
void robot_teste(unsigned int vel) {
  roda_centro.setSpeed(vel);
  roda_centro.run(FORWARD);

}
void robot_esquerda() {
  roda_esquerda.setSpeed(100);
  roda_esquerda.run(RELEASE);
  roda_direita.setSpeed(190);
  roda_direita.run(FORWARD);
}
void robot_direita() {
  roda_direita.setSpeed(150);
  roda_direita.run(RELEASE);
  roda_esquerda.setSpeed(150);
  roda_esquerda.run(FORWARD);
}
void robot_calibracao_esquerda()
{
  roda_esquerda.setSpeed(180);
  roda_esquerda.run(BACKWARD);
  roda_direita.setSpeed(180);
  roda_direita.run(FORWARD);

}
void robot_calibracao_direita()
{
  roda_esquerda.setSpeed(250);
  roda_esquerda.run(FORWARD);
  roda_direita.setSpeed(250);
  roda_direita.run(BACKWARD);

}

void robot_stop(unsigned char v)
{
  roda_esquerda.setSpeed(v);
  roda_esquerda.run(RELEASE);
  roda_direita.setSpeed(v);
  roda_direita.run(RELEASE);


} //end robot stop

Posicao procuraMelhorPosicao(Posicao minha, Posicao inimigo) {
  Posicao melhor = minha;
  int diferenca = verificaDiferenca( minha, inimigo);
  if (posicaoValida( {minha.linha - 1, minha.coluna } )) {
    if (verificaDiferenca({minha.linha - 1, minha.coluna }, inimigo) > diferenca) {
      melhor = {minha.linha - 1, minha.coluna };
      diferenca = verificaDiferenca({minha.linha - 1, minha.coluna }, inimigo);
    }
  } else if (posicaoValida( {minha.linha , minha.coluna - 1} )) {
    if (verificaDiferenca({minha.linha, minha.coluna - 1 }, inimigo) > diferenca) {
      melhor = {minha.linha, minha.coluna - 1};
      diferenca = verificaDiferenca({minha.linha , minha.coluna - 1}, inimigo);
    }
  } else if (posicaoValida( {minha.linha + 1 , minha.coluna } )) {
    if (verificaDiferenca({minha.linha + 1, minha.coluna }, inimigo) > diferenca) {
      melhor = {minha.linha + 1, minha.coluna};
      diferenca = verificaDiferenca({minha.linha + 1 , minha.coluna }, inimigo);
    }
  } else if (posicaoValida( {minha.linha , minha.coluna + 1 } )) {
    if (verificaDiferenca({minha.linha, minha.coluna + 1 }, inimigo) > diferenca) {
      melhor = {minha.linha, minha.coluna + 1};
    }
  }

  return melhor;
}

boolean enviaPosicao(Posicao p) {
  while (mySerial.read() != 100) {
    mySerial.write(p.linha + 30);
    delay(50);
    mySerial.write(p.coluna + 20);
  }
  return false;
}

boolean posicaoValida(Posicao p) {
  return p.linha <= CAMPO.linha && p.linha > 0 && p.coluna <= CAMPO.coluna && p.coluna > 0;
}

int verificaDiferenca(Posicao minha, Posicao destino) {
  return abs((destino.linha - minha.linha)) + abs((destino.coluna - minha.coluna));
}

void movimentaDestino(Posicao p){
  
}


