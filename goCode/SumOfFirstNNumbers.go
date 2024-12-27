//go:build sum

package main

import "fmt"

func main() {
	var n int
	fmt.Print("Enter a number: ")
	fmt.Scan(&n)
	var sum int
	for i := 1; i <= n; i++ {
		sum += i
	}
	fmt.Println(sum)
}
