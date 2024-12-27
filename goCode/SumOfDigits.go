//go:build sumOfDigits

package main

import "fmt"

func main() {
	var a int
	fmt.Scan(&a)
	var sum int
	for a > 0 {
		sum += a%10
		a /= 10
	}
	fmt.Println(sum)
}
