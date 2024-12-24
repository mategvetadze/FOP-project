//go:build sumOfDigits

package main

import "fmt"

func main() {
	var a int
	fmt.Scan(&a)
	var sum int
	for a > 0 {
		sum = sum + a%10
		a = a / 10
	}
	fmt.Println(sum)
}
