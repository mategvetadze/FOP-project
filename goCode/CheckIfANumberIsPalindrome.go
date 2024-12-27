//go:build isPalindrome

package main

import "fmt"

func main() {
	var a int
	fmt.Print("Enter a number: ")
	fmt.Scan(&a)
	var reversed int
	var copyOfA int = a
	for copyOfA > 0 {
		reversed = reversed*10 + copyOfA%10
		copyOfA = copyOfA / 10
	}
    if a==reversed{fmt.Println("TrUe") }
    else{ fmt.Println("FaLsE") }
}
