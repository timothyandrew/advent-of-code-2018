package main

import (
	"fmt"
	"io/ioutil"
	"strconv"
	"strings"
)

type Set struct {
	m map[int]bool
}

func (s *Set) add(n int) {
	s.m[n] = true
}

func (s Set) exists(n int) bool {
	return (s.m[n] == true)
}

func readFile(filename string) string {
	data, _ := ioutil.ReadFile(filename)
	return string(data)
}

func main() {
	input := strings.Split(readFile("input.txt"), "\n")

	sum := 0
	seen := Set{make(map[int]bool)}
	seen.add(0)

	for {
		for _, line := range input {
			number, _ := strconv.Atoi(line)
			sum += number

			if seen.exists(sum) {
				fmt.Println("The first repeat was", sum)
				return
			}

			seen.add(sum)
		}
	}

	fmt.Println(sum)
}
