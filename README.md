# 🎯 Cannon Game

Projeto desenvolvido para a disciplina de **Programação III**, baseado no **Capítulo 6 – Cannon Game App** do livro *Android 6 for Programmers: An App-Driven Approach (3rd Edition)*.

## 👥 Integrantes

- Gabriel Henrique
- Isaac Sousa

## 📱 Sobre o projeto

O Cannon Game é um jogo desenvolvido para Android em que o jogador controla um canhão através de toques na tela.

O objetivo é disparar projéteis e acertar os alvos, acumulando pontos.

O projeto busca aplicar na prática conceitos de desenvolvimento Android, como gráficos, animações, threads, interação por toque e efeitos sonoros.

## ✅ Funcionalidades implementadas

Até o momento foram implementadas:

- Estrutura inicial do projeto Android;
- Desenvolvimento em Java;
- `GameView` personalizado;
- Utilização de `SurfaceView`;
- Utilização de `SurfaceHolder`;
- Game Loop utilizando `Thread`;
- Desenho dos elementos utilizando `Canvas` e `Paint`;
- Canhão na parte inferior da tela;
- Alvo;
- Sistema de disparo;
- Projétil animado;
- Direção do disparo definida pelo toque do jogador;
- Detecção de colisão entre projétil e alvo;
- Sistema de pontuação;
- Reposicionamento do alvo após ser atingido;
- Execução e testes realizados no emulador Pixel 6a.

## 🎮 Como jogar

1. Execute o aplicativo.
2. Um alvo será exibido na tela.
3. Toque em uma posição da tela para realizar um disparo.
4. O projétil será lançado na direção do ponto tocado.
5. Acerte o alvo para ganhar pontos.
6. Ao acertar, o alvo muda de posição.

## 🛠️ Tecnologias utilizadas

- Android Studio
- Java
- Android SDK
- Canvas
- Paint
- SurfaceView
- SurfaceHolder
- Thread
- Git
- GitHub

## 🔊 Recursos em desenvolvimento

Os seguintes efeitos sonoros estão sendo preparados:

- `cannon_fire.wav` — disparo do canhão;
- `target_hit.wav` — acerto no alvo;
- `blocker_hit.wav` — colisão com obstáculo.

Também serão implementadas novas funcionalidades e melhorias durante as próximas etapas do desenvolvimento.

## ▶️ Como executar

1. Clone o repositório:

   git clone https://github.com/gfraga126-gif/CannonGame.git

2. Abra o projeto no Android Studio.
3. Aguarde a sincronização do Gradle.
4. Selecione um dispositivo Android ou emulador.
5. Execute o projeto utilizando **Run App**.

O projeto foi testado utilizando um emulador **Pixel 6a**.

## 📚 Disciplina

**Programação III**

Projeto acadêmico desenvolvido com o objetivo de aplicar conceitos de programação mobile Android.

## 🚧 Status do projeto

**Em desenvolvimento.**

A versão atual já possui o Game Loop, disparos, colisão com o alvo e sistema de pontuação funcionando.