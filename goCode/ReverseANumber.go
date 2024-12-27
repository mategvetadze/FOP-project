//go:build reverse

package main

import "fmt"

func main() {
	var a int
	fmt.Print("Enter a number: ")
	fmt.Scan(&a)
	var reversed int
	for a > 0 {
		reversed = reversed*10 + a%10
		a = a / 10
	}
	fmt.Println(reversed)
}
