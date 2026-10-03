# 🎯 Cannon Game

Projeto desenvolvido para a disciplina de **Programação III**, baseado no **Capítulo 6 – Cannon Game App** do livro *Android 6 for Programmers: An App-Driven Approach (3rd Edition)*.

## 👥 Integrantes

- Gabriel Henrique
- Isaac Sousa

## 📱 Sobre o projeto

O **Cannon Game** é um jogo desenvolvido para Android no qual o jogador utiliza um canhão para disparar projéteis em direção a um alvo.

O projeto tem como objetivo aplicar conceitos de programação mobile Android, incluindo gráficos com Canvas e Paint, SurfaceView, SurfaceHolder, threads, game loop, eventos de toque, colisões e reprodução de efeitos sonoros.

---

## ✅ Funcionalidades implementadas

Até o momento, o projeto possui:

- Estrutura do projeto Android criada no Android Studio;
- Desenvolvimento utilizando Java;
- GameView personalizado;
- SurfaceView e SurfaceHolder;
- Game Loop utilizando Thread;
- Atualização do jogo em aproximadamente 60 FPS;
- Desenho dos elementos utilizando Canvas e Paint;
- Canhão posicionado na parte inferior da tela;
- Controle de disparo através do toque;
- Cálculo da direção do projétil;
- Movimento do projétil;
- Alvo para o jogador acertar;
- Detecção de colisão entre projétil e alvo;
- Reposicionamento automático do alvo após um acerto;
- Sistema de pontuação;
- Obstáculo (blocker) móvel;
- Movimento horizontal do blocker;
- Detecção de colisão entre projétil e blocker;
- Cronômetro da partida;
- Partidas com duração de 60 segundos;
- Tela de Game Over;
- Exibição da pontuação final;
- Reinício da partida através de toque na tela;
- Execução de efeitos sonoros utilizando SoundPool.

---

## 🔊 Efeitos sonoros

Atualmente existem três efeitos sonoros funcionando no jogo:

### Disparo do canhão

Arquivo:

`cannon_fire.wav`

É reproduzido sempre que o jogador realiza um disparo.

### Acerto no alvo

Arquivo:

`target_hit.wav`

É reproduzido quando o projétil atinge o alvo.

### Acerto no blocker

Arquivo:

`blocker_hit.wav`

É reproduzido quando o projétil atinge o obstáculo móvel.

---

## 🎮 Como jogar

1. Inicie o aplicativo.
2. O jogador começa com **0 pontos** e **60 segundos**.
3. Toque em uma posição da tela para disparar.
4. O projétil seguirá na direção do ponto tocado.
5. Acerte o alvo para ganhar pontos.
6. Após um acerto, o alvo muda de posição.
7. Evite atingir o blocker móvel.
8. Se o projétil atingir o blocker, o disparo é interrompido.
9. Continue tentando marcar pontos até o tempo terminar.

---

## ⏱️ Cronômetro

Cada partida possui duração de:

`60 segundos`

Durante a partida, o tempo restante é exibido na tela.

Exemplo:

`Tempo: 60s`

`Tempo: 59s`

`Tempo: 58s`

`...`

`Tempo: 0s`

Quando o cronômetro chega a zero, a partida termina.

---

## 🏁 Game Over

Quando o tempo chega a zero:

- O jogo é interrompido;
- Novos disparos são bloqueados;
- O blocker para de se movimentar;
- A pontuação final é exibida;
- A tela mostra a mensagem **GAME OVER**.

Exemplo:

`GAME OVER`

`Pontuacao final: 5`

`Toque para jogar novamente`

Ao tocar na tela, uma nova partida é iniciada.

A pontuação volta para zero e o cronômetro retorna para 60 segundos.

---

## 🟧 Blocker

O jogo possui um obstáculo móvel chamado **Blocker**.

Ele se movimenta horizontalmente pela tela e muda automaticamente de direção ao chegar às extremidades.

Quando um projétil atinge o blocker:

- O projétil desaparece;
- O jogador não recebe ponto;
- O efeito `blocker_hit.wav` é reproduzido.

---

## 🏆 Sistema de pontuação

O jogador começa cada partida com:

`Pontos: 0`

Cada alvo atingido adiciona:

`+1 ponto`

A pontuação é exibida durante toda a partida e também na tela de Game Over.

---

## 🛠️ Tecnologias utilizadas

- Android Studio
- Java
- Android SDK
- Canvas
- Paint
- SurfaceView
- SurfaceHolder
- Thread
- SoundPool
- MotionEvent
- Git
- GitHub

---

## ▶️ Como executar

Clone o repositório:

`git clone https://github.com/gfraga126-gif/CannonGame.git`

Depois:

1. Abra o projeto no Android Studio;
2. Aguarde a sincronização do Gradle;
3. Selecione um dispositivo Android ou emulador;
4. Compile o projeto;
5. Execute utilizando **Run App**.

O projeto foi testado utilizando um emulador **Pixel 6a**.

---

## 📂 Principais arquivos

`MainActivity.java`

Responsável pela Activity principal e pela inicialização do GameView.

`GameView.java`

Contém a maior parte da lógica do jogo, incluindo:

- Game Loop;
- Thread;
- Canvas;
- desenhos;
- movimentação;
- disparos;
- colisões;
- pontuação;
- cronômetro;
- Game Over;
- efeitos sonoros.

`res/raw/`

Contém os efeitos sonoros utilizados pelo jogo:

- `cannon_fire.wav`
- `target_hit.wav`
- `blocker_hit.wav`

---

## 🚧 Status do projeto

**Em desenvolvimento.**

A estrutura principal do Cannon Game já está funcionando.

### Próximas etapas

- Melhorar visualmente o canhão;
- Implementar uma mira mais completa;
- Revisar o modo imersivo/tela cheia;
- Melhorar o controle de pausa e retomada;
- Implementar dificuldade progressiva;
- Realizar testes finais;
- Adicionar screenshot do jogo ao README;
- Revisar o projeto antes da entrega.

---

## 📚 Disciplina

**Programação III**

Projeto acadêmico desenvolvido com o objetivo de aplicar conceitos de desenvolvimento de jogos e programação mobile Android.