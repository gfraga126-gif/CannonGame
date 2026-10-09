# 🎯 Cannon Game

Projeto desenvolvido para a disciplina de *Programação III, baseado no **Capítulo 6 – Cannon Game App* do livro Android 6 for Programmers: An App-Driven Approach (3rd Edition).

## 👥 Integrantes

- Gabriel Henrique
- Isaac Sousa

## 📱 Sobre o projeto

O *Cannon Game* é um jogo desenvolvido para Android no qual o jogador utiliza um canhão para disparar projéteis em direção a um alvo dinâmico.

O projeto tem como objetivo aplicar conceitos de programação mobile Android, incluindo gráficos com Canvas e Paint, SurfaceView, SurfaceHolder, threads, game loop, eventos de toque, mecânicas de física/colisões e reprodução de efeitos sonoros.

---

## ✅ Funcionalidades implementadas

Até o momento, o projeto possui:

- Estrutura do projeto Android criada no Android Studio;
- Desenvolvimento utilizando Java;
- GameView personalizado com SurfaceView e SurfaceHolder;
- Game Loop utilizando Thread (atualização a aproximadamente 60 FPS);
- Desenho de formas geométricas e elementos utilizando Canvas e Paint;
- Canhão posicionado na parte inferior da tela;
- *Controle de mira por deslize (swipe)* com exibição de linha de trajetória;
- Movimento contínuo do projétil após o disparo;
- *Alvo móvel com movimentação senoidal* (zigue-zague contínuo);
- Detecção matemática de colisão entre projétil e alvo;
- Reposicionamento automático do alvo após um acerto;
- *Sistema de partículas* gerando uma explosão visual colorida ao acertar o alvo;
- Obstáculo (blocker) com movimentação horizontal independente;
- Detecção de colisão entre projétil e blocker;
- *Dificuldade progressiva:* a cada acerto, o blocker fica mais rápido e o alvo diminui de tamanho;
- Sistema de pontuação exibido no HUD;
- *Cronômetro de partida:* contagem regressiva de 60 segundos;
- *Tela de Game Over* com bloqueio de ações e exibição da pontuação final;
- Reinício completo da partida através de um toque na tela;
- Execução de efeitos sonoros assíncronos utilizando SoundPool.

---

## 🔊 Efeitos sonoros

Atualmente existem três efeitos sonoros funcionando no jogo:

### Disparo do canhão
Arquivo: cannon_fire.wav
É reproduzido sempre que o jogador solta o dedo da tela, realizando um disparo.

### Acerto no alvo
Arquivo: target_hit.wav
É reproduzido quando o projétil atinge o alvo, acompanhado da explosão de partículas.

### Acerto no blocker
Arquivo: blocker_hit.wav
É reproduzido quando o projétil atinge o obstáculo móvel.

---

## 🎮 Como jogar

1. Inicie o aplicativo.
2. O jogador começa com *0 pontos* e *60 segundos*.
3. *Toque na tela, arraste o dedo para mirar* (uma linha guia cinza aparecerá indicando a direção) e *solte para disparar*.
4. O projétil seguirá na direção definida pela mira.
5. Acerte o alvo (que flutua em zigue-zague) para ganhar pontos.
6. A cada acerto, o jogo fica mais difícil: o alvo diminui de tamanho, fica mais rápido e o blocker acelera.
7. Evite atingir o blocker laranja. Se o projétil bater nele, o disparo é destruído.
8. Continue tentando marcar a maior pontuação possível até o tempo terminar.

---

## ⏱️ Cronômetro

Cada partida possui duração de:
60 segundos

Durante a partida, o tempo restante é atualizado em tempo real no canto superior direito da tela.
Quando o cronômetro chega a zero, a partida é imediatamente encerrada.

---

## 🏁 Game Over

Quando o tempo chega a zero:

- O jogo é interrompido (alvo, partículas e disparos param);
- Novos disparos são bloqueados;
- A tela é escurecida e mostra a mensagem *GAME OVER*;
- A pontuação final obtida naquela rodada é destacada no centro da tela.

Exemplo:
GAME OVER
Pontuacao final: 5
Toque para jogar novamente

Ao tocar na tela, todo o estado do jogo é resetado: a pontuação volta para zero, o tamanho e a velocidade do alvo e do blocker voltam ao padrão inicial e o cronômetro retorna para 60 segundos.

---

## 🟧 Blocker

O jogo possui um obstáculo móvel chamado *Blocker*.
Ele movimenta-se horizontalmente pela tela de forma contínua e independente, mudando automaticamente de direção ao bater nas bordas.

Quando um projétil atinge o blocker:
- O projétil é destruído;
- O jogador não pontua;
- O efeito blocker_hit.wav é reproduzido.

---

## 🏆 Sistema de pontuação

O jogador começa cada partida com:
Pontos: 0

Cada alvo atingido adiciona:
+1 ponto

A pontuação é exibida durante toda a partida e também na tela de Game Over.

---

## 🛠️ Tecnologias utilizadas

- Android Studio
- Java / Android SDK
- Canvas e Paint (Renderização customizada)
- SurfaceView e SurfaceHolder
- Threads (Game Loop)
- SoundPool (Áudio de baixa latência)
- MotionEvent (Captura de gestos e swipe)
- Git / GitHub

---

## ▶️ Como executar

Clone o repositório:
git clone https://github.com/gfraga126-gif/CannonGame.git

Depois:

1. Abra o projeto no Android Studio;
2. Aguarde a sincronização do Gradle;
3. Selecione um dispositivo Android ou emulador;
4. Compile o projeto;
5. Execute utilizando *Run App*.

O projeto foi testado utilizando um emulador *Pixel 6a*.

---

## 📂 Principais arquivos

MainActivity.java
Responsável pela Activity principal e pela inicialização do GameView.

GameView.java
A classe core da aplicação. Contém toda a lógica estrutural, incluindo:
- Game Loop e instâncias de Thread;
- Renderização via Canvas;
- Matemática de movimentação senoidal e física de trajetórias;
- Classe auxiliar Particle para efeitos visuais;
- Detecção de colisões (Bounding boxes e raio de interseção);
- Gestão de estados (Partida e Game Over).

res/raw/
Contém os efeitos sonoros utilizados pelo jogo.

---

## 🚧 Status do projeto

*Na reta final de desenvolvimento.*
A base e as mecânicas avançadas do Cannon Game já estão totalmente implementadas.

### Próximas etapas

- Implementar salvamento de pontuação máxima (High Score) usando SharedPreferences;
- Melhorar visualmente o design do canhão no Canvas;
- Adicionar transição de cores dinâmica ao cenário (Tema Dia/Noite com base no tempo);
- Revisar o modo imersivo/tela cheia e os estados do ciclo de vida (pausa e retomada);
- Realizar os testes finais de estabilidade;
- Adicionar screenshot ou GIF do jogo rodando ao README;
- Revisão final do código para a entrega da disciplina.

---

## 📚 Disciplina

*Programação III*
Projeto acadêmico desenvolvido com o objetivo de aplicar conceitos avançados de desenvolvimento de jogos 2D e programação mobile para Android.