//go:build fibonacci

package main

import "fmt"

func main() {
	var n int
	var a int = 0
	var b int = 1
	fmt.Scan(&n)
	if n == 1 {
		fmt.Println(0)
	}
	if n == 2 {
		fmt.Println(1)
	}
	for i := 3; i <= n; i++ {
		var temp int = a
		a = b
		b = b + temp
	}
	fmt.Println(b)
}
