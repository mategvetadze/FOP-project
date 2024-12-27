//go:build isPrime

package main

import "fmt"

func main() {
	var a int
	fmt.Print("Enter a number: ")
	fmt.Scan(&a)
	var isPrime bool = true
	for i := 2; i < a; i++ {
		if a%i == 0 {
			isPrime = false
		}
	}
	fmt.Println(isPrime)
}
