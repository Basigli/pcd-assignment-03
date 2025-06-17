package main

import (
	"fmt"
	"math/rand"
	"time"
)

type Move int

type Message struct {
	playerMove Move
	playerName string
	result     string
}

const (
	ROCK = iota
	PAPER
	SCISSORS
)

func determineResult(m1, m2 Move) (string, string) {
	if m1 == m2 {
		return "draw", "draw"
	}
	switch {
	case (m1 == ROCK && m2 == SCISSORS) ||
		(m1 == PAPER && m2 == ROCK) ||
		(m1 == SCISSORS && m2 == PAPER):
		return "win", "lose"
	default:
		return "lose", "win"
	}
}
func referee(player1Channel, player2Channel chan Message) {

	// var response1Channel = make(chan Message)
	// var response2Channel = make(chan Message)

	for i := 0; ; i++ {
		fmt.Printf("----- Round %v -----\n", i)
		fmt.Println("[ Referee ]: Players make a move!")
		// sending messages to the players to make a move
		// player1Channel <- Message{responseChannel: response1Channel}
		// player2Channel <- Message{responseChannel: response2Channel}

		player1Channel <- Message{}
		player2Channel <- Message{}
		// wait for the moves

		response1 := <-player1Channel
		response2 := <-player2Channel
		// fmt.Printf("response1: %v\n", response1.playerMove)
		// fmt.Printf("response2: %v\n", response2.playerMove)
		fmt.Println("[ Referee ]: calculating result...")
		player1Result, player2Result := determineResult(response1.playerMove, response2.playerMove)
		// fmt.Println("Bot1 result: ", player1Result, "\n", "Bot2 result: ", player2Result)
		player1Channel <- Message{result: player1Result}
		player2Channel <- Message{result: player2Result}

		time.Sleep(10 * time.Second)
	}

	// determine the winner
	// communicate the winner
}

func player(name string, requestChannel chan Message) {
	var score int = 0
	var formattedPlayerName string = "[" + name + "]:"
	for {
		// wait for the request from the referee
		<-requestChannel

		fmt.Println(formattedPlayerName, " making a move")
		var move Move = Move(rand.Intn(3))

		switch move {
		case ROCK:
			fmt.Println(formattedPlayerName, " played ROCK")
		case PAPER:
			fmt.Println(formattedPlayerName, " played PAPER")
		case SCISSORS:
			fmt.Println(formattedPlayerName, " played SCISSORS")
		}

		// writing the response
		requestChannel <- Message{playerName: name, playerMove: move}

		// wait for the result from the referee

		request := <-requestChannel
		if request.result == "win" {
			score++
		}
		fmt.Println(formattedPlayerName, request.result, "!")
		fmt.Println(formattedPlayerName, " my score is ", score)
	}

}

func main() {
	var player1Channel = make(chan Message)
	var player2Channel = make(chan Message)

	go player("Bot1", player1Channel)
	go player("Bot2", player2Channel)
	go referee(player1Channel, player2Channel)

	select {}
}
