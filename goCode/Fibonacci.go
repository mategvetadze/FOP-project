//go:build fibonacci

package main

import "fmt"

func main() {
	var n int
	var a int = 0
	var b int = 1
	fmt.Print("Enter a number: ")
	fmt.Scan(&n)
	if n == 0 {
		fmt.Println(0)
	}
	if n == 1 {
		fmt.Println(1)
	}
	for i := 2; i <= n; i++ {
		var temp int = a
		a = b
		b = b + temp
	}
	fmt.Println(b)
}
