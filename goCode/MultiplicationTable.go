//go:build table

package main

import "fmt"

func main() {
	var a int
	fmt.Print("Enter a number: ")
	fmt.Scan(&a)
	for i := 1; i <= 10; i++ {
		fmt.Println(i * a)
	}
}
