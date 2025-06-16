package main

import (
	"fmt"
	"math/rand"
	"time"
)

type Move int

const (
	Rock Move = iota
	Paper
	Scissors
)

var moveNames = [...]string{"Rock", "Paper", "Scissors"}

func (m Move) String() string {
	return moveNames[m]
}

func randomMove() Move {
	return Move(rand.Intn(3))
}

// Player messages
type MoveRequest struct {
	responseChan chan Move
}

type ResultMessage struct {
	ownMove  Move
	opponent Move
	outcome  string // "win", "lose", "draw"
	newScore int
}

// Player goroutine
func player(name string, refereeRequestChan chan MoveRequest, refereeResultChan chan ResultMessage) {
	score := 0
	for {
		// Wait for move request
		req := <-refereeRequestChan
		move := randomMove()
		req.responseChan <- move

		// Wait for result
		result := <-refereeResultChan
		if result.outcome == "win" {
			score++
		}
		fmt.Printf("[%s] Played: %s, Opponent: %s → %s (Score: %d)\n",
			name, result.ownMove, result.opponent, result.outcome, score)
	}
}

// Referee goroutine
func referee(p1Req, p2Req chan MoveRequest, p1Res, p2Res chan ResultMessage) {
	for round := 1; ; round++ {
		fmt.Printf("\n--- Round %d ---\n", round)

		// Channels to receive player moves
		move1Chan := make(chan Move)
		move2Chan := make(chan Move)

		// Request moves
		p1Req <- MoveRequest{responseChan: move1Chan}
		p2Req <- MoveRequest{responseChan: move2Chan}

		// Receive moves
		move1 := <-move1Chan
		move2 := <-move2Chan

		// Determine outcome
		outcome1, outcome2 := determineOutcome(move1, move2)

		// Send results to players
		p1Res <- ResultMessage{ownMove: move1, opponent: move2, outcome: outcome1}
		p2Res <- ResultMessage{ownMove: move2, opponent: move1, outcome: outcome2}

		// Sleep between rounds
		time.Sleep(3 * time.Second)
	}
}

func determineOutcome(m1, m2 Move) (string, string) {
	if m1 == m2 {
		return "draw", "draw"
	}
	switch {
	case (m1 == Rock && m2 == Scissors) ||
		(m1 == Paper && m2 == Rock) ||
		(m1 == Scissors && m2 == Paper):
		return "win", "lose"
	default:
		return "lose", "win"
	}
}

func main() {
	rand.Seed(time.Now().UnixNano())

	// Channels for communication
	p1Request := make(chan MoveRequest)
	p2Request := make(chan MoveRequest)
	p1Result := make(chan ResultMessage)
	p2Result := make(chan ResultMessage)

	// Start goroutines
	go player("Player 1", p1Request, p1Result)
	go player("Player 2", p2Request, p2Result)
	go referee(p1Request, p2Request, p1Result, p2Result)

	// Let it run forever
	select {}
}
