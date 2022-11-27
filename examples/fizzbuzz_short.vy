₁⟑₍₃₅kF½*∑∴,

# ₁ pushes the number 100 to the stack
# The ⟑ starts a eagerly-evaluated mapping lambda that:
#   ₍₃₅ pushes the divisibility of each number by 3 and 5 to the stack
#   kF½ pushes the list ["Fizz", "Buzz"] to the stack
#   * pushes the product of the two lists to the stack
#   The ∑ reduces that product to a single string
#   The ∴ gets the maximum item between the string and the argument
#   The , prints the result