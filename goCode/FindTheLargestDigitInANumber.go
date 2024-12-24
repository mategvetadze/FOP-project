//go:build largestDigit

package main

import "fmt"

func main() {
	var a int
	fmt.Scan(&a)
	var max int = a % 10
	for a > 0 {
		if a%10 > max {
			max = a % 10
		}
		a = a / 10
	}
	fmt.Println(max)
}
