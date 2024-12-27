//go:build factorial

package main

import "fmt"

func main() {
	var n int
	fmt.Print("Enter a number: ")
	fmt.Scan(&n)
	var factorial int = 1
	for i := 1; i <= n; i++ {
		factorial = factorial * i
	}
	fmt.Println(factorial)
}
