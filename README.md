# 🎯 Cannon Game

## Sobre o projeto

Este projeto foi desenvolvido por nós para a disciplina de **Programação III**, com base no Capítulo 6 do livro *Android 6 for Programmers: An App-Driven Approach (3rd Edition)*.

A ideia foi criar um jogo para Android em que o jogador controla um canhão e precisa acertar um alvo que fica se movimentando pela tela.

Durante o desenvolvimento, trabalhamos com Java, Canvas, SurfaceView, threads, detecção de colisões, controles por toque e efeitos sonoros. Também fomos acrescentando algumas melhorias para deixar o jogo mais interessante.

## 👥 Integrantes

- Gabriel Henrique
- Isaac Sousa

## 📱 Funcionalidades do jogo

O Cannon Game conta com as seguintes funcionalidades:

- Menu inicial com o nome do jogo e o recorde do jogador;
- Canhão que acompanha a direção do dedo na tela;
- Disparo de projéteis ao soltar o dedo;
- Alvo que se movimenta de um lado para o outro;
- Obstáculo móvel que pode bloquear os tiros;
- Sistema de colisão entre o projétil, o alvo e o obstáculo;
- Pontuação de 1 ponto por alvo acertado;
- Bônus de 5 segundos a cada acerto;
- Aumento da dificuldade conforme o jogador faz pontos;
- Efeitos de partículas quando o alvo é atingido;
- Sons para disparos, acertos no alvo e colisões com o obstáculo;
- Recorde salvo no celular;
- Tela de Game Over com opção de jogar novamente;
- Fundo espacial com estrelas e planetas;
- Animação de pontos ao acertar o alvo;
- Contagem regressiva em destaque nos últimos 10 segundos.

## 🌌 Fundo espacial

Uma das melhorias que fizemos foi colocar um fundo espacial no jogo.

O cenário tem estrelas, planetas e cores mais escuras, combinando com a proposta do Cannon Game.

O fundo foi feito utilizando o próprio Canvas, sem precisar adicionar imagens externas. Ele aparece no menu inicial, durante a partida e na tela de Game Over.

## ⭐ Sistema de pontuação

O jogador começa com zero pontos.

Cada vez que acerta o alvo, recebe:

- **+1 ponto**
- **+5 segundos no cronômetro**

Também adicionamos uma animação que mostra a mensagem **+1 PONTO!** no meio da tela por alguns instantes.

A pontuação total continua aparecendo na parte superior da tela.

Conforme o jogador vai acertando os alvos, a dificuldade aumenta. O alvo fica menor e mais rápido, e o obstáculo também aumenta sua velocidade.

## ⏱️ Cronômetro e contagem regressiva

Cada partida começa com **60 segundos**.

O tempo restante aparece na parte superior da tela e vai diminuindo durante o jogo.

Quando faltam 10 segundos para acabar, aparece uma contagem regressiva grande no centro da tela, mostrando os números de 10 até 1.

Os números aparecem em vermelho e possuem uma pequena animação para chamar a atenção do jogador.

Se o jogador acertar um alvo e ganhar mais tempo, a contagem se ajusta automaticamente.

Quando o tempo chega a zero, a partida termina.

## 🔊 Efeitos sonoros

Adicionamos três sons ao jogo para deixar as ações mais interessantes.

**Disparo do canhão:** `cannon_fire.wav`

Esse som é reproduzido quando o jogador dispara um projétil.

**Acerto no alvo:** `target_hit.wav`

É reproduzido quando o jogador acerta o alvo e ganha um ponto.

**Acerto no obstáculo:** `blocker_hit.wav`

É reproduzido quando o projétil bate no obstáculo.

Os sons foram implementados utilizando SoundPool.

## 🎮 Como jogar

1. Abra o aplicativo e toque na tela inicial para começar.
2. O jogo começa com 60 segundos e zero pontos.
3. Toque na tela e arraste o dedo para escolher a direção do canhão.
4. Solte o dedo para disparar.
5. Tente acertar o alvo vermelho que fica se movimentando.
6. Evite o obstáculo laranja, pois ele bloqueia os disparos.
7. Cada acerto vale um ponto e acrescenta cinco segundos.
8. Tente conseguir a maior pontuação antes que o tempo termine.

## 🏁 Game Over e recorde

Quando o tempo acaba, aparece a tela de **Game Over**, mostrando a pontuação final.

O jogo também verifica se o jogador conseguiu superar seu recorde anterior.

Quando isso acontece, aparece a mensagem **NOVO RECORDE!** com uma animação.

Utilizamos SharedPreferences para salvar o recorde no próprio celular. Assim, mesmo fechando e abrindo o aplicativo novamente, a maior pontuação continua registrada.

Para iniciar outra partida, basta tocar na tela.

## 🛠️ Tecnologias utilizadas

Durante o desenvolvimento utilizamos:

- Android Studio;
- Java;
- Android SDK;
- Canvas e Paint para desenhar os elementos;
- SurfaceView e SurfaceHolder;
- Thread para controlar o funcionamento do jogo;
- MotionEvent para os controles por toque;
- SoundPool para os efeitos sonoros;
- SharedPreferences para salvar o recorde;
- Git e GitHub para organizar o código e registrar as alterações.

## ▶️ Como executar o projeto

Primeiro, clone o repositório:

```bash
git clone https://github.com/gfraga126-gif/CannonGame.git
```

Depois:

1. Abra a pasta do projeto no Android Studio.
2. Aguarde o Gradle terminar de sincronizar.
3. Escolha um emulador ou celular Android.
4. Clique em **Run** para executar o aplicativo.

Durante o desenvolvimento, utilizamos o emulador Pixel 6a para realizar os testes.

## 📂 Organização do projeto

**MainActivity.java**

É responsável por iniciar o aplicativo e carregar a tela do jogo.

**GameView.java**

É o arquivo principal do jogo. Nele estão o desenho dos elementos, a movimentação do canhão, o funcionamento dos tiros, as colisões, a pontuação, o cronômetro e as animações.

**res/raw/**

Essa pasta contém os três arquivos de áudio utilizados no jogo.

**README.md**

Contém as informações sobre o projeto, as funcionalidades e as instruções para executar o aplicativo.

## 🚧 Situação atual do projeto

Conseguimos implementar as principais funcionalidades do Cannon Game e também acrescentamos algumas melhorias além da versão inicial.

Entre elas estão o fundo espacial, os efeitos sonoros, o sistema de recorde, as partículas, a animação de pontos e a contagem regressiva.

Também realizamos testes no emulador para verificar o funcionamento do jogo.

Antes da entrega, vamos conferir se todos os arquivos estão atualizados no GitHub e fazer uma última revisão do projeto.

## 📚 Disciplina

**Programação III**

O desenvolvimento deste trabalho ajudou a colocar em prática os conteúdos estudados sobre programação Android, gráficos 2D, eventos de toque, threads e organização de projetos utilizando Git e GitHub.